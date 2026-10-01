package com.vietphan.bank_service.service.impl;

import com.vietphan.bank_service.DTO.request.AdminCreateUserRequest;
import com.vietphan.bank_service.DTO.request.AdminUpdateUserRequest;
import com.vietphan.bank_service.DTO.response.AdminUserDetailResponse;
import com.vietphan.bank_service.DTO.response.AdminUserResponse;
import com.vietphan.bank_service.DTO.response.CardResponse;
import com.vietphan.bank_service.constant.RedisConstants;
import com.vietphan.bank_service.entity.*;
import com.vietphan.bank_service.enums.AccountStatus;
import com.vietphan.bank_service.enums.AccountType;
import com.vietphan.bank_service.enums.Role;
import com.vietphan.bank_service.exception.AppException;
import com.vietphan.bank_service.exception.Errors;
import com.vietphan.bank_service.mapper.AccountMapper;
import com.vietphan.bank_service.mapper.BalanceMapper;
import com.vietphan.bank_service.mapper.CardMapper;
import com.vietphan.bank_service.repository.*;
import com.vietphan.bank_service.security.CustomUserDetails;
import com.vietphan.bank_service.service.AdminUserService;
import com.vietphan.bank_service.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;
    private final CardRepository cardRepository;
    private final UserLevelRepository userLevelRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountMapper accountMapper;
    private final BalanceMapper balanceMapper;
    private final CardMapper cardMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserResponse> getAllUsers(String levelName, Role role) {
        List<User> users;
        boolean hasLevel = levelName != null && !levelName.isBlank();
        boolean hasRole = role != null;

        if (hasLevel && hasRole) {
            users = userRepository.findByLevel_LevelNameAndRole(levelName.trim(), role);
        } else if (hasLevel) {
            users = userRepository.findByLevel_LevelName(levelName.trim());
        } else if (hasRole) {
            users = userRepository.findByRole(role);
        } else {
            users = userRepository.findAll();
        }

        return users.stream().map(this::toAdminUserResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserDetailResponse getUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(Errors.USER_NOT_FOUND));

        Account account = user.getAccount();
        List<CardResponse> cardResponses = Collections.emptyList();
        com.vietphan.bank_service.DTO.response.BalanceResponse balanceResponse = null;
        
        if (account != null) {
            List<Balance> balances = balanceRepository.findByAccount(account);
            double totalAvailable = balances.stream().mapToDouble(Balance::getAvailableBalance).sum();
            double totalHold = balances.stream().mapToDouble(Balance::getHoldBalance).sum();
            balanceResponse = com.vietphan.bank_service.DTO.response.BalanceResponse.builder()
                    .accountId(account.getAccountId())
                    .availableBalance(totalAvailable)
                    .holdBalance(totalHold)
                    .build();

            cardResponses = cardRepository.findByAccount(account).stream()
                    .map(cardMapper::toResponse)
                    .toList();
        }

        return AdminUserDetailResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .fullName(account != null ? account.getCustomerName() : null)
                .email(account != null ? account.getEmail() : null)
                .phoneNumber(account != null ? account.getPhoneNumber() : null)
                .role(user.getRole())
                .levelId(user.getLevel() != null ? user.getLevel().getLevelId() : null)
                .levelName(user.getLevel() != null ? user.getLevel().getLevelName() : null)
                .account(account != null ? accountMapper.toResponse(account) : null)
                .balance(balanceResponse)
                .cards(cardResponses)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public AdminUserResponse createUser(AdminCreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(Errors.USER_ALREADY_EXISTS);
        }

        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new AppException(Errors.EMAIL_ALREADY_EXISTS);
        }

        if (accountRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(Errors.PHONE_NUMBER_ALREADY_EXISTS);
        }

        UserLevel level = null;
        if (request.getLevelId() != null) {
            level = userLevelRepository.findById(request.getLevelId())
                    .orElseThrow(() -> new AppException(Errors.USER_LEVEL_NOT_FOUND));
        } else {
            level = userLevelRepository.findByLevelName("VIP1").orElse(null);
        }

        Account account = Account.builder()
                .customerName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .accountType(AccountType.PAYMENT)
                .status(AccountStatus.ACTIVE)
                .currency("VND")
                .build();
        Account savedAccount = accountRepository.save(account);



        User user = User.builder()
                .account(savedAccount)
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .level(level)
                .build();
        User savedUser = userRepository.save(user);

        return toAdminUserResponse(savedUser);
    }

    @Override
    @Transactional
    public AdminUserResponse updateUser(Long userId, AdminUpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(Errors.USER_NOT_FOUND));

        Account account = user.getAccount();
        if (account != null) {
            if (request.getEmail() != null && !request.getEmail().isBlank()) {
                String newEmail = request.getEmail().trim();
                if (accountRepository.existsByEmailAndAccountIdNot(newEmail, account.getAccountId())) {
                    throw new AppException(Errors.EMAIL_ALREADY_EXISTS);
                }
                account.setEmail(newEmail);
            }

            if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
                String newPhone = request.getPhoneNumber().trim();
                if (accountRepository.existsByPhoneNumberAndAccountIdNot(newPhone, account.getAccountId())) {
                    throw new AppException(Errors.PHONE_NUMBER_ALREADY_EXISTS);
                }
                account.setPhoneNumber(newPhone);
            }

            if (request.getFullName() != null && !request.getFullName().isBlank()) {
                account.setCustomerName(request.getFullName().trim());
            }

            accountRepository.save(account);
            evictCache(account.getAccountId());
        }

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        if (request.getLevelId() != null) {
            UserLevel level = userLevelRepository.findById(request.getLevelId())
                    .orElseThrow(() -> new AppException(Errors.USER_LEVEL_NOT_FOUND));
            user.setLevel(level);
        }

        User updatedUser = userRepository.save(user);
        return toAdminUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(Errors.USER_NOT_FOUND));

        try {
            CustomUserDetails currentUser = SecurityUtils.getCurrentUserDetails();
            if (currentUser != null && currentUser.getUser() != null && currentUser.getUser().getUserId().equals(userId)) {
                throw new AppException(Errors.CANNOT_DELETE_ADMIN_USER);
            }
        } catch (AppException e) {
            if (e.getError() == Errors.CANNOT_DELETE_ADMIN_USER) {
                throw e;
            }
            log.warn("Cannot verify current user in SecurityUtils: {}", e.getMessage());
        }

        Account account = user.getAccount();
        if (account != null) {
            List<Card> cards = cardRepository.findByAccount(account);
            if (cards.stream().anyMatch(Card::isHasPendingTransactions)) {
                throw new AppException(Errors.CARD_HAS_PENDING_TRANSACTIONS);
            }

            List<Balance> balances = balanceRepository.findByAccount(account);
            for (Balance balance : balances) {
                if (balance.getAvailableBalance() != 0.0 || balance.getHoldBalance() != 0.0) {
                    throw new AppException(Errors.ACCOUNT_HAS_NON_ZERO_BALANCE);
                }
            }

            refreshTokenRepository.deleteByUser(user);

            if (!balances.isEmpty()) {
                balanceRepository.deleteAll(balances);
            }

            if (!cards.isEmpty()) {
                cardRepository.deleteAll(cards);
            }

            userRepository.delete(user);
            accountRepository.delete(account);
            evictCache(account.getAccountId());
        } else {
            refreshTokenRepository.deleteByUser(user);
            userRepository.delete(user);
        }
    }

    private AdminUserResponse toAdminUserResponse(User user) {
        Account account = user.getAccount();
        return AdminUserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .fullName(account != null ? account.getCustomerName() : null)
                .email(account != null ? account.getEmail() : null)
                .phoneNumber(account != null ? account.getPhoneNumber() : null)
                .role(user.getRole())
                .levelId(user.getLevel() != null ? user.getLevel().getLevelId() : null)
                .levelName(user.getLevel() != null ? user.getLevel().getLevelName() : null)
                .accountId(account != null ? account.getAccountId() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private void evictCache(Long accountId) {
        if (accountId == null) return;
        try {
            redisTemplate.delete(RedisConstants.ACCOUNT_CACHE_PREFIX + accountId);
            redisTemplate.delete(RedisConstants.BALANCE_CACHE_PREFIX + accountId);
        } catch (Exception e) {
            log.warn("Failed to delete cache for accountId {}: {}", accountId, e.getMessage());
        }
    }
}
