package com.supergroup.kos.dto.data;

import com.supergroup.kos.building.domain.constant.seamap.BossSeaType;
import com.supergroup.kos.building.domain.constant.seamap.ResourceIslandType;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.model.seamap.BossWillGetReward;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ElementsConfigResponse {
    private Long id;

    private Long           level;
    private String         name;
    private String         thumbnail;
    private Long           width;
    private Long           height;
    private Long           length;
    private SeaElementType type;

    // boss
    private BossSeaType       bossType;
    private Long              bossAtk1;
    private Long              bossAtk2;
    private Long              bossHp;
    private Double            bossDef1;
    private Double            bossDef2;
    private Double            bossDodge;
    private Long              bossTimeRespawn; //seconds
    private BossWillGetReward willGet;

    // resource
    private ResourceIslandType resourceType;
    private Long               resourceCapacity;
    private Double             resourceExploitSpeed;
}
