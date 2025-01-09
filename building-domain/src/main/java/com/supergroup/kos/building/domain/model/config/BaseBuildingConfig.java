package com.supergroup.kos.building.domain.model.config;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.BuildingName;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@MappedSuperclass
@Accessors(chain = true)
public class BaseBuildingConfig extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long         id;
    private BuildingName name;
    private Long         level;
    private Long         wood;
    private Long         stone;
    private Long         gold;
    private Long         upgradeDuration; // millis
    private Long         gpPointReward;
}
