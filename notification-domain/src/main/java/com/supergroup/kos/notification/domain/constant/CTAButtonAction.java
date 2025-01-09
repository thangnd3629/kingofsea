package com.supergroup.kos.notification.domain.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CTAButtonAction {
    NAVIGATE_CASTLE("NAVIGATE:CASTLE");

    private String action;
}
