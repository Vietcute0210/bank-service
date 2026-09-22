package com.vietphan.bank_service.service.impl;

import com.vietphan.bank_service.DTO.request.LoginRequest;
import com.vietphan.bank_service.DTO.request.RefreshTokenRequest;
import com.vietphan.bank_service.DTO.request.RegisterRequest;
import com.vietphan.bank_service.DTO.response.AuthResponse;
import com.vietphan.bank_service.entity.Account;
import com.vietphan.bank_service.entity.Balance;
import com.vietphan.bank_service.entity.RefreshToken;
import com.vietphan.bank_service.entity.User;
import com.vietphan.bank_service.enums.AccountStatus;
import com.vietphan.bank_service.enums.AccountType;
import com.vietphan.bank_service.enums.Role;
import com.vietphan.bank_service.exception.AppException;
import com.vietphan.bank_service.exception.Errors;
import com.vietphan.bank_service.repository.AccountRepository;
import com.vietphan.bank_service.repository.BalanceRepository;
import com.vietphan.bank_service.repository.RefreshTokenRepository;
import com.vietphan.bank_service.repository.UserRepository;
import com.vietphan.bank_service.security.CustomUserDetails;
import com.vietphan.bank_service.service.AuthService;
import com.vietphan.bank_service.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshExpiration;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if(userRepository.existsByUsername(request.getUsername())){
            throw new AppException(Errors.ACCOUNT_ALREADY_EXISTS);
        }
        // xay account moi khi dang ki
        Account account = Account.builder()
                .customerName(request.getUsername())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .accountType(AccountType.PAYMENT)
                .status(AccountStatus.ACTIVE)
                .currency("VND")
                .build();
        Account savedAccount = accountRepository.save(account);

        // khoi tao balance voi account moi
        Balance balance = Balance.builder()
                .account(savedAccount)
                .availableBalance(0.0)
                .holdBalance(0.0)
                .build();
        balanceRepository.save(balance);

        // khoi tao user lien ket account
        User user = User.builder()
                .account(savedAccount)
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);

        // tao jwt
        CustomUserDetails userDetails =  new CustomUserDetails(user);
        String accessToken = jwtService.generateToken(userDetails, savedAccount.getAccountId());
        String refreshToken = createAndSaveRefreshToken(user, userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .accountId(user.getAccount().getAccountId())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(Errors.ACCOUNT_NOT_FOUND));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtService.generateToken(userDetails, user.getAccount().getAccountId());
        String refreshToken = createAndSaveRefreshToken(user, userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accountId(user.getAccount().getAccountId())
                .username(user.getUsername())
                .build();
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String tokenStr = request.getRefreshToken();
        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new AppException(Errors.REFRESHTOKEN_NOT_FOUND));
        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new AppException(Errors.REFRESHTOKEN_REVOKED);
        }
        User user = refreshToken.getUser();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        // cap access token moi
        String newAccessToken = jwtService.generateToken(userDetails, user.getAccount().getAccountId());
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(tokenStr)
                .username(user.getUsername())
                .accountId(user.getAccount().getAccountId())
                .build();
    }

    @Override
    @Transactional
    public void logout(String refreshTokenStr) {
        if (refreshTokenStr != null && !refreshTokenStr.isBlank()) {
            refreshTokenRepository.findByToken(refreshTokenStr).ifPresent(refreshTokenRepository::delete);
        }
    }

    private String createAndSaveRefreshToken(User user, CustomUserDetails userDetails) {
        // Xoa token cu roi moi tao cai moi
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
        String tokenStr = jwtService.generateRefreshToken(userDetails);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(tokenStr)
                .expiryDate(Instant.now().plusMillis(refreshExpiration))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
        return tokenStr;
    }
}
