package com.supergroup.kos.dto.item;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.supergroup.kos.building.domain.constant.item.ItemId;
import com.supergroup.kos.building.domain.dto.seamap.CoordinatesDTO;
import com.supergroup.kos.building.domain.service.seamap.item.TypeApplySpeedItem;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UseItemRequest {
    @NotNull
    private ItemId             itemId;
    @Min(value = 1L)
    private Long               amount;
    private Long               upgradeSessionId;
    private TypeApplySpeedItem typeApplySpeedItem;
    // use wa 12 item
    private CoordinatesDTO     newLocation;
}
