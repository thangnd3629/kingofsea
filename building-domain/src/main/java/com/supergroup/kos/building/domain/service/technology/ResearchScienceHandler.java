package com.supergroup.kos.building.domain.service.technology;

import java.util.ArrayList;
import java.util.List;

import com.supergroup.kos.building.domain.command.GetArmoryBuildingInfoCommand;
import com.supergroup.kos.building.domain.command.GetCommunityBuildingInfo;
import com.supergroup.kos.building.domain.command.GetScoutBuildingInfoCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.TechnologyType;
import com.supergroup.kos.building.domain.constant.research.FeatureType;
import com.supergroup.kos.building.domain.constant.research.TargetType;
import com.supergroup.kos.building.domain.constant.research.UnitType;
import com.supergroup.kos.building.domain.model.research.ResearchResult;
import com.supergroup.kos.building.domain.model.technology.UserTechnology;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingRepository;
import com.supergroup.kos.building.domain.service.building.ArmoryBuildingService;
import com.supergroup.kos.building.domain.service.building.CommunityBuildingService;
import com.supergroup.kos.building.domain.service.building.ScoutBuildingService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResearchScienceHandler implements ResearchHandler {
    private final BuildingRepository       buildingRepository;
    private final KosProfileService        kosProfileService;
    private final ArmoryBuildingService    armoryBuildingService;
    private final CommunityBuildingService communityBuildingService;
    private final ScoutBuildingService     scoutBuildingService;

    @Override
    public List<ResearchResult> research(UserTechnology userTechnology) {
        var kosProfile = userTechnology.getResearchBuilding().getKosProfile();
        var kosProfileId = kosProfile.getId();
        var technology = userTechnology.getTechnology();
        switch (userTechnology.getTechnology().getCode()) {
            case SC1:
                var result = new ArrayList<ResearchResult>();
                // unlock storage
                var r1 = createDefaultResult(technology);
                var goldStorage = buildingRepository.get(BuildingName.STORAGE_GOLD, kosProfileId);
                goldStorage.setIsLock(false);
                result.add(r1.setValue(goldStorage.getName()));

                var r2 = createDefaultResult(technology);
                var stoneStorage = buildingRepository.get(BuildingName.STORAGE_STONE, kosProfileId);
                stoneStorage.setIsLock(false);
                result.add(r2.setValue(stoneStorage.getName()));

                var r3 = createDefaultResult(technology);
                var woodStorage = buildingRepository.get(BuildingName.STORAGE_WOOD, kosProfileId);
                woodStorage.setIsLock(false);
                result.add(r3.setValue(woodStorage.getName()));

                // save
                buildingRepository.save(goldStorage);
                buildingRepository.save(stoneStorage);
                buildingRepository.save(woodStorage);

                return result;
            case SC2:
            case SC3:
            case SC4:
            case SC6:
            case SC8:
            case SC10:
            case SC13:
            case SC15:
                var resUnlockBuilding = new ArrayList<ResearchResult>();
                for (BuildingName buildingName : userTechnology.getTechnology().getUnLockListBuildingName()) {
                    // unlock building
                    var building = buildingRepository.get(buildingName, kosProfileId);
                    building.setIsLock(false);

                    resUnlockBuilding.add(new ResearchResult().setTargetType(TargetType.UNLOCK_BUILDING)
                                                              .setUnitType(UnitType.NONE)
                                                              .setValue(building.getName()));
                    // save
                    buildingRepository.save(building);
                }
                return resUnlockBuilding;
            case SC5:
                // unlock military & advanced tech
                kosProfile.setIsUnlockMilitaryTech(true);
                kosProfile.setIsUnlockAdvancedMilitaryTech(true);

                kosProfileService.saveProfile(kosProfile);
                return List.of(new ResearchResult().setTargetType(technology.getTargetType())
                                                   .setUnitType(technology.getUnitType())
                                                   .setValue(TechnologyType.MILITARY),
                               new ResearchResult().setTargetType(technology.getTargetType())
                                                   .setUnitType(technology.getUnitType())
                                                   .setValue(TechnologyType.ADVANCE_MILITARY));
            case SC9:
            case SC18:
                // unlock 3 slot community building
                var communityBuilding = communityBuildingService.getBuildingInfo(new GetCommunityBuildingInfo(kosProfileId));
                communityBuilding.setMaxListingRelic(userTechnology.getTechnology().getMaxListingRelic());

                // save
                var resSc18 = createDefaultResult(technology);
                buildingRepository.save(communityBuilding);
                return List.of(resSc18.setValue(userTechnology.getTechnology().getMaxListingRelic()));
            case SC14:
                // unlock merge weapon feature
                var armoryBuilding = armoryBuildingService.getBuildingInfo(new GetArmoryBuildingInfoCommand(kosProfileId));
                armoryBuilding.setIsLockMergeWeapon(false);

                // save
                var resSc14 = createDefaultResult(technology);
                buildingRepository.save(armoryBuilding);
                kosProfileService.saveProfile(kosProfile);
                return List.of(resSc14.setValue(FeatureType.MERGE_WEAPON));
            case SC16:
                var resSc16 = createDefaultResult(technology);
                kosProfile.setCanUseSpeedItem(true);
                return List.of(resSc16.setValue(FeatureType.USE_SPEED_ITEM));

            case SC20:
                // reduce upgrading time percent
                var resSc20 = createDefaultResult(technology);
                kosProfile.addReduceUpgradingTimePercent(userTechnology.getTechnology().getReduceUpgradingTimePercent());
                kosProfileService.saveProfile(kosProfile);
                return List.of(resSc20.setValue(userTechnology.getTechnology().getReduceUpgradingTimePercent()));
            case SC21:
                var resSc21 = createDefaultResult(technology);
                kosProfile.addBonusProtectResourcePercent(userTechnology.getTechnology().getBonusProtectResourcePercent());
                kosProfileService.saveProfile(kosProfile);
                return List.of(resSc21.setValue(technology.getBonusProtectResourcePercent()));
            case SC23:
                // unlock scout all info feature
                var scoutBuilding = scoutBuildingService.getBuildingInfo(new GetScoutBuildingInfoCommand(kosProfileId));
                scoutBuilding.setUnlockScoutFeature(true);

                // save
                var resSc23 = createDefaultResult(technology);
                buildingRepository.save(scoutBuilding);
                return List.of(resSc23.setValue(FeatureType.SCOUT));

            default:
                // unlock technology without effect
                return List.of();
        }
    }
}
