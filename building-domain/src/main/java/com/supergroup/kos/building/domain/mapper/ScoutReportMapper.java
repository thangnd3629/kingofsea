package com.supergroup.kos.building.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.supergroup.kos.building.domain.model.ship.EscortShip;
import com.supergroup.kos.building.domain.model.ship.EscortShipScoutingResult;
import com.supergroup.kos.building.domain.model.ship.MotherShip;
import com.supergroup.kos.building.domain.model.ship.MotherShipScoutingResult;

@Mapper
public interface ScoutReportMapper {
    @Mapping(target = "level", source = "motherShipLevelConfig.level")
    @Mapping(target = "percentStatLevel", source = "motherShipLevelConfig.percentStat")
    @Mapping(target = "cmd", source = "motherShipConfigQualityConfig.motherShipConfig.cmd")
    @Mapping(target = "tng", source = "motherShipConfigQualityConfig.motherShipConfig.tng")
    @Mapping(target = "speed", source = "motherShipConfigQualityConfig.motherShipConfig.speed")
    @Mapping(target = "type", source = "motherShipConfigQualityConfig.motherShipConfig.type")
    @Mapping(target = "thumbnail", source = "motherShipConfigQualityConfig.motherShipConfig.thumbnail")
    @Mapping(target = "name", source = "motherShipConfigQualityConfig.motherShipConfig.name")
    @Mapping(target = "atk1", source = "motherShipConfigQualityConfig.motherShipConfig.atk1")
    @Mapping(target = "atk2", source = "motherShipConfigQualityConfig.motherShipConfig.atk2")
    @Mapping(target = "def1", source = "motherShipConfigQualityConfig.motherShipConfig.def1")
    @Mapping(target = "def2", source = "motherShipConfigQualityConfig.motherShipConfig.def2")
    @Mapping(target = "hp", source = "motherShipConfigQualityConfig.motherShipConfig.hp")
    @Mapping(target = "dodge", source = "motherShipConfigQualityConfig.motherShipConfig.dodge")
    @Mapping(target = "quality", source = "motherShipConfigQualityConfig.motherShipQualityConfig.quality")
    @Mapping(target = "percentStatQuality", source = "motherShipConfigQualityConfig.motherShipQualityConfig.percentStat")
    MotherShipScoutingResult toMotherShipScoutingResult(MotherShip motherShip);
    List<MotherShipScoutingResult> toMotherShipScoutingResults(List<MotherShip> motherShips);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "level", source = "level")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "thumbnail", source = "escortShipConfig.thumbnail")
    @Mapping(target = "type", source = "escortShipConfig.type")
    @Mapping(target = "percentStatQuality", source = "escortShipGroup.escortShipGroupLevelConfig.percentStat")
    @Mapping(target = "groupName", source = "escortShipGroup.escortShipGroupLevelConfig.escortShipGroupConfig.name")
    @Mapping(target = "quality", source = "escortShipGroup.escortShipGroupLevelConfig.level")
    @Mapping(target = "name", source = "escortShipConfig.name")
    @Mapping(target = "atk1", source = "escortShipConfig.atk1")
    @Mapping(target = "atk2", source = "escortShipConfig.atk2")
    @Mapping(target = "def1", source = "escortShipConfig.def1")
    @Mapping(target = "def2", source = "escortShipConfig.def2")
    @Mapping(target = "hp", source = "escortShipConfig.hp")
    @Mapping(target = "dodge", source = "escortShipConfig.dodge")
    EscortShipScoutingResult toEscortShipScoutingResult(EscortShip escortShip);
    List<EscortShipScoutingResult> toEscortShipScoutingResults(List<EscortShip> escortShips);
}
