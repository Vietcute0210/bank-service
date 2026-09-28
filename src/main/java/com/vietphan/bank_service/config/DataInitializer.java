package com.vietphan.bank_service.config;

import com.vietphan.bank_service.entity.Account;
import com.vietphan.bank_service.entity.Balance;
import com.vietphan.bank_service.entity.User;
import com.vietphan.bank_service.entity.UserLevel;
import com.vietphan.bank_service.enums.AccountStatus;
import com.vietphan.bank_service.enums.AccountType;
import com.vietphan.bank_service.enums.Role;
import com.vietphan.bank_service.repository.AccountRepository;
import com.vietphan.bank_service.repository.BalanceRepository;
import com.vietphan.bank_service.repository.UserLevelRepository;
import com.vietphan.bank_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;
    private final UserLevelRepository userLevelRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        initUserLevels();
        initAdminUser();
    }

    private void initUserLevels() {
        if (!userLevelRepository.existsByLevelName("VIP1")) {
            userLevelRepository.save(UserLevel.builder()
                    .levelName("VIP1")
                    .cardLimit(3)
                    .dailyTransferLimit(200_000.0)
                    .build());
            log.info("Initialized default level VIP1");
        }
        if (!userLevelRepository.existsByLevelName("VIP2")) {
            userLevelRepository.save(UserLevel.builder()
                    .levelName("VIP2")
                    .cardLimit(5)
                    .dailyTransferLimit(500_000.0)
                    .build());
            log.info("Initialized default level VIP2");
        }
        if (!userLevelRepository.existsByLevelName("VIP3")) {
            userLevelRepository.save(UserLevel.builder()
                    .levelName("VIP3")
                    .cardLimit(10)
                    .dailyTransferLimit(1_000_000.0)
                    .build());
            log.info("Initialized default level VIP3");
        }
    }

    private void initAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            Account account = accountRepository.findByEmail("admin@bank.com")
                    .orElseGet(() -> accountRepository.save(Account.builder()
                            .customerName("System Administrator")
                            .email("admin@bank.com")
                            .phoneNumber("0900000000")
                            .accountType(AccountType.PAYMENT)
                            .status(AccountStatus.ACTIVE)
                            .currency("VND")
                            .build()));

            balanceRepository.findByAccount(account).orElseGet(() ->
                    balanceRepository.save(Balance.builder()
                            .account(account)
                            .availableBalance(100_000_000.0)
                            .holdBalance(0.0)
                            .build())
            );

            UserLevel vip3 = userLevelRepository.findByLevelName("VIP3").orElse(null);

            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("Admin@123456"))
                    .role(Role.ADMIN)
                    .account(account)
                    .level(vip3)
                    .build();

            userRepository.save(admin);
            log.info("Default admin user initialized successfully (username: admin, password: Admin@123456, role: ADMIN)");
        }
    }
}
