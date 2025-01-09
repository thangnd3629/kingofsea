package com.supergroup.kos.building.domain.task;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpgradeTask {
    private  Long upgradeSessionId;
}
