package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.constant.item.ItemId;
import com.supergroup.kos.building.domain.constant.item.ItemType;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.service.seamap.item.TypeApplySpeedItem;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UseItemCommand {
    private Long               kosProfileId;
    private ItemId             itemId;
    private Long               amount;
    private Long               upgradeSessionId;
    private ItemType           itemType;
    private TypeApplySpeedItem typeApplySpeedItem;
    private Coordinates        newLocation;
}
