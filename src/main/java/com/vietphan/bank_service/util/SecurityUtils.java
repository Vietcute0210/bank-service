package com.vietphan.bank_service.util;

import com.vietphan.bank_service.exception.AppException;
import com.vietphan.bank_service.exception.Errors;
import com.vietphan.bank_service.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static CustomUserDetails getCurrentUserDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new AppException(Errors.ACCOUNT_NOT_FOUND);
        }
        return userDetails;
    }

    public static Long getCurrentAccountId(){
        Long accountId = getCurrentUserDetails().getAccountId();
        if(accountId == null){
            throw  new AppException(Errors.ACCOUNT_NOT_FOUND);
        }
        return accountId;
    }
}
