package com.supergroup.auth.domain.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String encode(String password) {
        return encoder.encode(password);
    }

    public boolean check(String actual, String encodedPassword) {
        return encoder.matches(actual, encodedPassword);
    }
}
