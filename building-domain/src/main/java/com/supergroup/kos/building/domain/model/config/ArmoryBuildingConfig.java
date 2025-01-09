package com.supergroup.kos.building.domain.model.config;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.kos.building.domain.constant.EscortShipGroupLevel;
import com.supergroup.kos.building.domain.constant.EscortShipGroupName;
import com.supergroup.kos.building.domain.constant.WeaponSetLevel;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_armory_building_config", indexes = @Index(columnList = "level"))
@Getter
@Setter
@Accessors(chain = true)
public class ArmoryBuildingConfig extends BaseBuildingConfig {

    private Long researchLevelRequired;

    @Transient
    private WeaponSetLevel       unLockWeaponSetLevel;
    @Transient
    private EscortShipGroupLevel unLockEscortShipGroupLevel;
    @Transient
    private EscortShipGroupName  unLockEscortShipGroupName;

}
