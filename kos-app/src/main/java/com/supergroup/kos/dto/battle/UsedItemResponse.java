package com.supergroup.kos.dto.battle;

import java.util.List;

import com.supergroup.kos.building.domain.constant.item.ItemId;
import com.supergroup.kos.building.domain.dto.seamap.CoordinatesDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UsedItemResponse {
    private String         username;
    private String         avatarUrl;
    private CoordinatesDTO coordinates;
    private List<ItemId>   items;
}
