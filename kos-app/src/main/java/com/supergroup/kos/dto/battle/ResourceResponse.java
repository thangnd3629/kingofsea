package com.supergroup.kos.dto.battle;

import java.util.List;

import com.supergroup.kos.building.domain.constant.item.ItemId;
import com.supergroup.kos.building.domain.model.battle.OccupyEffect;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ResourceResponse {
    private Long         wood;
    private Long         stone;
    private Long         gold;
    private List<Long>   queens; // this is queen model id
    private List<Long>   relics; // this is relic model id
    private List<Long>   weapons;
    private List<ItemId> items;
    private Double       gloryPoint;
    private OccupyEffect effect;
}
