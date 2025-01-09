package com.supergroup.auth.domain.util;

import java.util.Random;

public class OTPUtils {

    /**
     * Generate random otp code
     * */
    public static String code() {
        Random random = new Random();
        StringBuilder otpBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            otpBuilder.append(random.nextInt(10));
        }
        return otpBuilder.toString();
    }
}
