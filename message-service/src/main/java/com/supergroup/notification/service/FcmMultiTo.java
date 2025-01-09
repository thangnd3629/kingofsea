package com.supergroup.notification.service;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FcmMultiTo extends To {
    private final List<String> tokens;
}
