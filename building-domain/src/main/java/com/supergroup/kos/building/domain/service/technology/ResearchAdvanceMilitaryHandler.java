package com.supergroup.kos.building.domain.service.technology;

import java.util.List;
import java.util.stream.Collectors;

import com.supergroup.kos.building.domain.command.GetCommandBuildingInfo;
import com.supergroup.kos.building.domain.constant.EscortShipGroupName;
import com.supergroup.kos.building.domain.constant.research.FeatureType;
import com.supergroup.kos.building.domain.model.research.ResearchResult;
import com.supergroup.kos.building.domain.model.technology.Technology;
import com.supergroup.kos.building.domain.model.technology.UserTechnology;
import com.supergroup.kos.building.domain.service.building.CommandBuildingService;
import com.supergroup.kos.building.domain.service.ship.EscortShipService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResearchAdvanceMilitaryHandler implements ResearchHandler {

    private final CommandBuildingService    commandBuildingService;
    private final EscortShipService         escortShipService;

    @Override
    public List<ResearchResult> research(UserTechnology userTechnology) {
        var kosProfileId = userTechnology.getResearchBuilding().getKosProfile().getId();
        var technology = userTechnology.getTechnology();
        var researchResult = new ResearchResult()
                .setTargetType(technology.getTargetType())
                .setUnitType(technology.getUnitType());
        switch (technology.getCode()) {
            // Unlock buy mother ship
            case AM1:
                unlockBuyMotherShip(kosProfileId);
                return List.of(researchResult.setValue(FeatureType.BUY_MOTHER_SHIP));

            // Unlock upgradable level Escort Ship
            case AM5:
            case AM6:
            case AM7:
            case AM9:
            case AM10:
            case AM11:
            case AM14:
            case AM15:
            case AM16:
                unlockUpgradableLevelEscortShip(kosProfileId, technology);
                return List.of(researchResult.setValue(technology.getMaxLevelEscortShip()));
            default:
                // unlock technology without effect
                return List.of();
        }
    }

    private void unlockBuyMotherShip(Long kosProfileId) {
        var commandBuilding = commandBuildingService.getBuildingInfo(new GetCommandBuildingInfo(kosProfileId));
        commandBuilding.setIsLockBuyMother(false);
        commandBuildingService.save(commandBuilding);
    }

    private void unlockUpgradableLevelEscortShip(Long kosProfileId, Technology technology) {
        var escortShips = escortShipService.getEscortShipsByGroupShip(kosProfileId,
                                                                      EscortShipGroupName.valueOf(technology.getTargetType().name()))
                                           .stream().peek(escortShip -> {
                    var maxLevel = technology.getMaxLevelEscortShip();
                    escortShip.setMaxLevel(maxLevel);
                }).collect(Collectors.toList());
        escortShipService.saveAll(escortShips);
    }
}
