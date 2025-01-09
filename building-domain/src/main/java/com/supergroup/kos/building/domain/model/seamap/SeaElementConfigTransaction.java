package com.supergroup.kos.building.domain.model.seamap;

import javax.persistence.Embedded;

import com.supergroup.kos.building.domain.constant.seamap.BossSeaType;
import com.supergroup.kos.building.domain.constant.seamap.ResourceIslandType;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SeaElementConfigTransaction {
    private Long           id;
    private Long           level;
    private String         name;
    private String         thumbnail;
    @Embedded
    private OccupiedArea   occupied;
    private SeaElementType seaElementType;

    // user base
    // resource
    private ResourceIslandType resourceType;
    private Double             resourceCapacity;
    private Double             resourceExploitSpeed;
    //boss
    private BossSeaType        bossType;
    private Long               bossAtk1;
    private Long               bossAtk2;
    private Long               bossHp;
    private Double             bossDef1;
    private Double             bossDef2;
    private Double             bossDodge;
    private Long               bossTimeRespawn;
    //ship
    //anchor
}
