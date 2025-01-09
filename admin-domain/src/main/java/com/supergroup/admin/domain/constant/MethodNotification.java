package com.supergroup.admin.domain.constant;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum MethodNotification {
    USER("USER"), LEAGUE("LEAGUE"), ALL("ALL");

    public static MethodNotification of(String method) {
        return Arrays.stream(MethodNotification.values()).filter(methodNotification -> methodNotification.method.equals(method))
                     .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public static Boolean exist(String method) {
        return Arrays.stream(MethodNotification.values()).anyMatch(methodNotification -> methodNotification.method.equals(method));
    }

    private String method;
}
