package com.supergroup.auth.domain.validator;

import java.util.regex.Pattern;

import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Strings;

import io.vavr.collection.List;

public class InputFieldValidator {

    public static final String EMAIL_REGEX    =
            "(?:[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    public static final String PHONE_REGEX    = "^\\+[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$";
    public static final String PASSWORD_REGEX = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,20}$";
    public static final String OTP_REGEX      = "[0-9]{6}";
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]{4,20}$";

    public boolean isValidEmail(String email) {
        if (Strings.isNullOrEmpty(email)) {
            return false;
        }
        var pattern = Pattern.compile(EMAIL_REGEX);
        var matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean isValidPhone(String phoneNumber) {
        if (Strings.isNullOrEmpty(phoneNumber)) {
            return false;
        }
        var patter = Pattern.compile(PHONE_REGEX);
        var matcher = patter.matcher(phoneNumber);
        return matcher.matches();
    }

    public boolean isValidOtp(String otp) {
        if (Strings.isNullOrEmpty(otp)) {
            return false;
        }
        var pattern = Pattern.compile(OTP_REGEX);
        var matcher = pattern.matcher(otp);
        return matcher.matches();
    }

    public boolean isValidAvatar(MultipartFile avatarFile) {
        if (avatarFile == null) {
            return false;
        }
        var contentType = avatarFile.getContentType();
        var whiteListContentType = List.of("image/png", "image/jpeg", "image/jpg");
        if (!whiteListContentType.contains(contentType)) {
            return false;
        }
        return avatarFile.getSize() <= 104857600;
    }

    public boolean isValidUsername(String username) {
        if (Strings.isNullOrEmpty(username)) {
            return false;
        }
        var pattern = Pattern.compile(USERNAME_REGEX);
        var matcher = pattern.matcher(username);
        return matcher.matches();
    }

    public boolean isValidPassword(String password) {
        if (Strings.isNullOrEmpty(password)) {
            return false;
        }
        var pattern = Pattern.compile(PASSWORD_REGEX);
        var matcher = pattern.matcher(password);
        return matcher.matches();
    }

}
