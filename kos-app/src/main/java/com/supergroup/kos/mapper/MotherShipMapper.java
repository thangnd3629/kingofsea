package com.supergroup.kos.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.supergroup.kos.building.domain.model.ship.MotherShip;
import com.supergroup.kos.dto.ship.MotherShipResponse;

@Mapper(uses = { MotherShipConfigMapper.class, WeaponMapper.class, WeaponSetMapper.class })
public interface MotherShipMapper {
    @Mapping(target = "model", source = "motherShip.motherShipConfigQualityConfig.motherShipConfig")
    @Mapping(target = "levelInfo", source = "motherShip.motherShipLevelConfig")
    @Mapping(target = "qualityInfo", source = "motherShip.motherShipConfigQualityConfig.motherShipQualityConfig")
    @Mapping(target = "technologyUnlockSlot.name", source = "motherShip.technologyRequiredUnlockSlotWeapon.technology.name")
    @Mapping(target = "technologyUnlockSlot.code", source = "motherShip.technologyRequiredUnlockSlotWeapon.technology.code")
    @Mapping(target = "technologyUnlockSlot.type", source = "motherShip.technologyRequiredUnlockSlotWeapon.technology.technologyType")
    @Mapping(target = "technologyUnlockSlot.isResearched", source = "motherShip.technologyRequiredUnlockSlotWeapon.isResearched")
    @Mapping(target = "isHealing", expression = "java(motherShip.isHealing())")
    MotherShipResponse toDTO(MotherShip motherShip);

    List<MotherShipResponse> toDTOs(Collection<MotherShip> motherShips);

}
