package com.supergroup.kos.building.domain.model.config;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.kos.building.domain.constant.MotherShipQualityKey;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_command_building_config", indexes = @Index(columnList = "level"))
@Getter
@Setter
@Accessors(chain = true)
public class CommandBuildingConfig extends BaseBuildingConfig {
    private Long                 slotMotherShip;
    private Long                 researchLevelRequired;
    @Transient
    private Long                 unLockMotherShipLevel;
    @Transient
    private MotherShipQualityKey unLockMotherShipQuality;
}
