package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.constant.EscortShipGroupName;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpgradeEscortShipGroupCommand {
    private EscortShipGroupName group;
    private Long kosProfileId;
}
