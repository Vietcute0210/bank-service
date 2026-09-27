package com.vietphan.bank_service.service.impl;

import com.vietphan.bank_service.DTO.request.AdminCreateUserRequest;
import com.vietphan.bank_service.DTO.request.AdminUpdateUserRequest;
import com.vietphan.bank_service.DTO.response.AdminUserDetailResponse;
import com.vietphan.bank_service.DTO.response.AdminUserResponse;
import com.vietphan.bank_service.constant.RedisConstants;
import com.vietphan.bank_service.entity.*;
import com.vietphan.bank_service.enums.AccountStatus;
import com.vietphan.bank_service.enums.AccountType;
import com.vietphan.bank_service.enums.Role;
import com.vietphan.bank_service.exception.AppException;
import com.vietphan.bank_service.exception.Errors;
import com.vietphan.bank_service.mapper.AdminUserMapper;
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
    private final AdminUserMapper adminUserMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserResponse> getAllUsers(String levelName, Role role) {
        boolean hasLevel = levelName != null && !levelName.isBlank();
        List<User> users = (hasLevel && role != null)
                ? userRepository.findByLevel_LevelNameAndRole(levelName.trim(), role)
                : hasLevel ? userRepository.findByLevel_LevelName(levelName.trim())
                : (role != null) ? userRepository.findByRole(role)
                : userRepository.findAll();

        return users.stream().map(adminUserMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserDetailResponse getUserDetail(Long userId) {
        User user = findUserById(userId);
        Account account = user.getAccount();
        Balance balance = (account != null) ? balanceRepository.findByAccount(account).orElse(null) : null;
        List<Card> cards = (account != null) ? cardRepository.findByAccount(account) : Collections.emptyList();

        return adminUserMapper.toDetailResponse(user, balance, cards);
    }

    @Override
    @Transactional
    public AdminUserResponse createUser(AdminCreateUserRequest request) {
        validateUniqueFields(request.getUsername(), request.getEmail(), request.getPhoneNumber());

        Account account = accountRepository.save(Account.builder()
                .customerName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .accountType(AccountType.PAYMENT)
                .status(AccountStatus.ACTIVE)
                .currency("VND")
                .build());

        balanceRepository.save(Balance.builder()
                .account(account)
                .availableBalance(0.0)
                .holdBalance(0.0)
                .build());

        User user = userRepository.save(User.builder()
                .account(account)
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .level(resolveUserLevel(request.getLevelId()))
                .build());

        return adminUserMapper.toResponse(user);
    }

    @Override
    @Transactional
    public AdminUserResponse updateUser(Long userId, AdminUpdateUserRequest request) {
        User user = findUserById(userId);
        Account account = user.getAccount();

        if (account != null) {
            updateAccountInfo(account, request);
        }

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        if (request.getLevelId() != null) {
            user.setLevel(resolveUserLevel(request.getLevelId()));
        }

        return adminUserMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = findUserById(userId);
        validateDeletable(user);

        Account account = user.getAccount();
        refreshTokenRepository.deleteByUser(user);

        if (account != null) {
            List<Card> cards = cardRepository.findByAccount(account);
            if (!cards.isEmpty()) {
                cardRepository.deleteAll(cards);
            }
            balanceRepository.findByAccount(account).ifPresent(balanceRepository::delete);
            userRepository.delete(user);
            accountRepository.delete(account);
            evictCache(account.getAccountId());
        } else {
            userRepository.delete(user);
        }
    }


    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(Errors.USER_NOT_FOUND));
    }

    private UserLevel resolveUserLevel(Long levelId) {
        return (levelId != null)
                ? userLevelRepository.findById(levelId).orElseThrow(() -> new AppException(Errors.USER_LEVEL_NOT_FOUND))
                : userLevelRepository.findByLevelName("VIP1").orElse(null);
    }

    private void validateUniqueFields(String username, String email, String phone) {
        if (userRepository.existsByUsername(username)) {
            throw new AppException(Errors.USER_ALREADY_EXISTS);
        }
        if (accountRepository.existsByEmail(email)) {
            throw new AppException(Errors.EMAIL_ALREADY_EXISTS);
        }
        if (accountRepository.existsByPhoneNumber(phone)) {
            throw new AppException(Errors.PHONE_NUMBER_ALREADY_EXISTS);
        }
    }

    private void updateAccountInfo(Account account, AdminUpdateUserRequest request) {
        Long accountId = account.getAccountId();

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = request.getEmail().trim();
            if (accountRepository.existsByEmailAndAccountIdNot(newEmail, accountId)) {
                throw new AppException(Errors.EMAIL_ALREADY_EXISTS);
            }
            account.setEmail(newEmail);
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            String newPhone = request.getPhoneNumber().trim();
            if (accountRepository.existsByPhoneNumberAndAccountIdNot(newPhone, accountId)) {
                throw new AppException(Errors.PHONE_NUMBER_ALREADY_EXISTS);
            }
            account.setPhoneNumber(newPhone);
        }

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            account.setCustomerName(request.getFullName().trim());
        }

        accountRepository.save(account);
        evictCache(accountId);
    }

    private void validateDeletable(User user) {
        try {
            CustomUserDetails currentUser = SecurityUtils.getCurrentUserDetails();
            if (currentUser != null && currentUser.getUser() != null && user.getUserId().equals(currentUser.getUser().getUserId())) {
                throw new AppException(Errors.CANNOT_DELETE_ADMIN_USER);
            }
        } catch (AppException e) {
            if (e.getError() == Errors.CANNOT_DELETE_ADMIN_USER) {
                throw e;
            }
            log.warn("Cannot verify current user in SecurityUtils: {}", e.getMessage());
        }

        Account account = user.getAccount();
        if (account == null) {
            return;
        }

        if (cardRepository.findByAccount(account).stream().anyMatch(Card::isHasPendingTransactions)) {
            throw new AppException(Errors.CARD_HAS_PENDING_TRANSACTIONS);
        }

        balanceRepository.findByAccount(account).ifPresent(balance -> {
            if (balance.getAvailableBalance() != 0.0 || balance.getHoldBalance() != 0.0) {
                throw new AppException(Errors.ACCOUNT_HAS_NON_ZERO_BALANCE);
            }
        });
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
