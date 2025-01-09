package com.supergroup.kos.dto.upgrade;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpgradeStatusResponse {
    private Long upgradeSessionId;
    private Long duration; // millis
    private Long current; // millis
}
