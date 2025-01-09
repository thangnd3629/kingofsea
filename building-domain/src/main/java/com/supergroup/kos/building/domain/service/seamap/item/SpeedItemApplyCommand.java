package com.supergroup.kos.building.domain.service.seamap.item;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SpeedItemApplyCommand extends ApplyItemCommand {
    private Long               kosProfileId;
    private Long               upgradeSessionId;
    private TypeApplySpeedItem typeApplySpeedItem;
}
