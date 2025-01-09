package com.supergroup.kos.building.domain.service.technology;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.supergroup.kos.building.domain.command.GetCommandBuildingInfo;
import com.supergroup.kos.building.domain.constant.EscortShipGroupName;
import com.supergroup.kos.building.domain.constant.research.TargetType;
import com.supergroup.kos.building.domain.model.research.ResearchResult;
import com.supergroup.kos.building.domain.model.ship.EscortShip;
import com.supergroup.kos.building.domain.model.technology.Technology;
import com.supergroup.kos.building.domain.model.technology.UserTechnology;
import com.supergroup.kos.building.domain.service.building.CommandBuildingService;
import com.supergroup.kos.building.domain.service.ship.EscortShipService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResearchMilitaryHandler implements ResearchHandler {

    private final CommandBuildingService commandBuildingService;
    private final EscortShipService      escortShipService;

    @Override
    public List<ResearchResult> research(UserTechnology userTechnology) {
        var kosProfileId = userTechnology.getResearchBuilding().getKosProfile().getId();
        var technology = userTechnology.getTechnology();
        var researchResult = new ResearchResult()
                .setTargetType(technology.getTargetType())
                .setUnitType(technology.getUnitType());
        switch (technology.getCode()) {
            // Unlock Slot Weapon Mother Ship
            case MT5:
            case MT13:
                unlockSlotWeaponMotherShip(kosProfileId, technology.getMaxSlotWeaponOfMotherShip());
                return List.of(researchResult.setValue(technology.getMaxSlotWeaponOfMotherShip()));

            // Discount Speed Build EscortShip
            case MT2:
            case MT3:
            case MT4:
            case MT6:
            case MT7:
            case MT8:
            case MT9:
            case MT14:
            case MT15:
            case MT16:
            case MT17:
                discountSpeedBuildEscortShip(kosProfileId, technology);
                return List.of(researchResult.setValue(technology.getDiscountPercentDurationTime()));
            // Discount RSS Build EscortShip
            case MT10:
            case MT18:
            case MT11:
            case MT19:
            case MT12:
            case MT20:
                discountRssBuildEscortShip(kosProfileId, technology);
                return List.of(researchResult.setValue(technology.getDiscountPercentRss()));
            default:
                // unlock technology without effect
                return List.of();
        }
    }

    private void unlockSlotWeaponMotherShip(Long kosProfileId, Long slotUnlock) {
        var commandBuilding = commandBuildingService.getBuildingInfo(new GetCommandBuildingInfo(kosProfileId));
        commandBuilding.setMaxSlotWeaponOfMotherShip(slotUnlock);
        commandBuildingService.save(commandBuilding);
    }

    private void discountRssBuildEscortShip(Long kosProfileId, Technology technology) {
        var escortShips = escortShipService.getEscortShipsByGroupShip(kosProfileId,
                                                                      EscortShipGroupName.valueOf(technology.getTargetType().name()))
                                           .stream().map(escortShip -> {
                    var percentRssBuildUpdated = escortShip.getPercentRssBuild() * (1 - technology.getDiscountPercentRss());
                    return escortShip.setPercentRssBuild(percentRssBuildUpdated);
                }).collect(Collectors.toList());
        escortShipService.saveAll(escortShips);
    }

    private void discountSpeedBuildEscortShip(Long kosProfileId, Technology technology) {
        var escortShips = new ArrayList<EscortShip>();
        if (technology.getTargetType().equals(TargetType.SPEED_BUILD_ESCORT_SHIP)) {
            escortShips = (ArrayList<EscortShip>) escortShipService.getEscortShips(kosProfileId);
        } else {
            escortShips = (ArrayList<EscortShip>) escortShipService.getEscortShipsByGroupShip(kosProfileId, EscortShipGroupName.valueOf(technology.getTargetType().name()));
        }
        var escortShipsUpdated = escortShips.stream().peek(escortShip -> {
            var percentSpeedBuildUpdated = escortShip.getPercentSpeedBuild() * (1 - technology.getDiscountPercentDurationTime());
             escortShip.setPercentSpeedBuild(percentSpeedBuildUpdated);
        }).collect(Collectors.toList());
        escortShipService.saveAll(escortShipsUpdated);
    }
}
