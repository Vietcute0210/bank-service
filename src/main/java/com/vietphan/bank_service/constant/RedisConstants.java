package com.vietphan.bank_service.constant;

public final class RedisConstants {

    private RedisConstants() {
        // Private constructor to prevent instantiation
    }

    public static final String ACCOUNT_CACHE_PREFIX = "account:";
    public static final String BALANCE_CACHE_PREFIX = "balance:account:";
}
