package com.supergroup.notification.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FcmTo extends To {
    private final String token;
}
