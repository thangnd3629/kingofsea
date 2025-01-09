package com.supergroup.kos.building.domain.model.battle.logic;

import java.io.Serializable;
import java.util.List;

import com.supergroup.kos.building.domain.constant.item.ItemId;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Belligerent implements Serializable {
    private Long         battleProfileId;
    private Long         kosProfileId;
    private List<ItemId> itemIds;
}
