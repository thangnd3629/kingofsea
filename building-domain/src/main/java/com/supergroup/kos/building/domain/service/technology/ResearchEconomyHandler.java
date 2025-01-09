package com.supergroup.kos.building.domain.service.technology;

import java.util.ArrayList;
import java.util.List;

import com.supergroup.kos.building.domain.model.research.ResearchResult;
import com.supergroup.kos.building.domain.model.technology.UserTechnology;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingRepository;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResearchEconomyHandler implements ResearchHandler {

    private final BuildingRepository buildingRepository;

    private final KosProfileService kosProfileService;

    @Override
    public List<ResearchResult> research(UserTechnology userTechnology) {
        var technology = userTechnology.getTechnology();
        var kosProfile = userTechnology.getResearchBuilding().getKosProfile();
        var researchResult =  new ResearchResult()
                .setTargetType(technology.getTargetType())
                .setUnitType(technology.getUnitType());
        switch (technology.getCode()) {
            case EC1:
                var result = new ArrayList<ResearchResult>();
                // unlock vault building
                technology.getUnLockListBuildingName().forEach(name -> {
                    var building = buildingRepository.get(name, kosProfile.getId());
                    // save
                    building.setIsLock(false);
                    buildingRepository.save(building);
                    result.add(researchResult.setValue(building.getName()));
                });
                return result;
            case EC2:
                // buff wood production up to 5%
            case EC7:
                // buff wood production up to 10%
                kosProfile.addBonusWoodProductionPercent(technology.getBonusWoodProductionPercent());
                kosProfileService.saveProfile(kosProfile);
                return List.of(researchResult.setValue(technology.getBonusWoodProductionPercent()));
            case EC3:
                // buff stone production up to 5%
            case EC8:
                // buff stone production up to 10%
                kosProfile.addBonusStoneProductionPercent(technology.getBonusStoneProductionPercent());
                kosProfileService.saveProfile(kosProfile);
                return List.of(researchResult.setValue(technology.getBonusStoneProductionPercent()));
            case EC4:
            case EC20:
                // buff gold production up to 5%
                kosProfile.addBonusGoldProductionPercent(technology.getBonusGoldProductionPercent());
                kosProfileService.saveProfile(kosProfile);
                return List.of(researchResult.setValue(technology.getBonusGoldProductionPercent()));
            case EC9:
                // buff capacity gold storage
                kosProfile.addBonusCapGoldStoragePercent(technology.getBonusCapGoldStoragePercent());
                kosProfileService.saveProfile(kosProfile);
                return List.of(researchResult.setValue(technology.getBonusCapGoldStoragePercent()));
            case EC11:
            case EC21:
                // buff effect relic item
                kosProfile.addBonusEffectRelicItemPercent(technology.getBonusEffectRelicItemPercent());
                kosProfileService.saveProfile(kosProfile);
                return List.of(researchResult.setValue(technology.getBonusEffectRelicItemPercent()));
            case EC13:
                // buff upgrading time
                kosProfile.addReduceUpgradingTimePercent(technology.getReduceUpgradingTimePercent());
                kosProfileService.saveProfile(kosProfile);
                return List.of(researchResult.setValue(technology.getReduceUpgradingTimePercent()));
            case EC6:
            case EC19:
                kosProfile.addBonusCapStoneStoragePercent(technology.getBonusCapStoneStoragePercent());
                kosProfile.addBonusCapWoodStoragePercent(technology.getBonusCapWoodStoragePercent());
                kosProfile.addBonusCapGoldStoragePercent(technology.getBonusCapGoldStoragePercent());
                kosProfileService.saveProfile(kosProfile);
                return List.of(createDefaultResult(technology));
            default:
                // unlock technology without effect
                return List.of();
        }
    }
}
