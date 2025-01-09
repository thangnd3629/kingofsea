package com.supergroup.kos.building.domain.model.config;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.supergroup.kos.building.domain.constant.MotherShipTypeKey;
import com.supergroup.kos.building.domain.constant.TechnologyCode;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_mother_ship_config")
@Getter
@Setter
@Accessors(chain = true)
public class MotherShipConfig extends BaseShipConfig {
    private Long              cmd;
    private Long              tng;
    private Long              speed; // unit is distance per second
    private MotherShipTypeKey type;
    private String            thumbnail;
    private Double            recoverySpeed; // unit is hp per second
    @Enumerated(EnumType.STRING)
    private TechnologyCode    technologyRequirement;
}
