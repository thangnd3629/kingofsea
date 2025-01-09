package com.supergroup.kos.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.supergroup.kos.config.AccessSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AuthUtil {

    public static UserDetails getCurrentUserDetails() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // getUserName return email
    }

    public static Long getUserId() {
        return ((AccessSession) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId(); // getUserName return email
    }

}
