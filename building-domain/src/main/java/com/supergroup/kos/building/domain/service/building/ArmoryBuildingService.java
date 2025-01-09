package com.supergroup.kos.building.domain.service.building;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetArmoryBuildingInfoCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.building.ArmoryBuilding;
import com.supergroup.kos.building.domain.model.config.BaseBuildingConfig;
import com.supergroup.kos.building.domain.repository.persistence.building.ArmoryBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.weapon.WeaponSetRepository;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.technology.TechnologyService;

@Service
public class ArmoryBuildingService extends BaseBuildingService {

    private final ArmoryBuildingRepository armoryBuildingRepository;
    private final BuildingConfigDataSource buildingConfigDataSource;
    private final WeaponSetRepository      weaponSetRepository;
    private final TechnologyService        technologyService;

    public ArmoryBuildingService(@Autowired ArmoryBuildingRepository armoryBuildingRepository,
                                 @Autowired BuildingConfigDataSource buildingConfigDataSource,
                                 @Autowired WeaponSetRepository weaponSetRepository,
                                 @Autowired KosProfileService kosProfileService,
                                 @Autowired TechnologyService technologyService) {
        super(kosProfileService, buildingConfigDataSource);
        this.armoryBuildingRepository = armoryBuildingRepository;
        this.buildingConfigDataSource = buildingConfigDataSource;
        this.weaponSetRepository = weaponSetRepository;
        this.technologyService = technologyService;
    }

    /**
     * Get building info
     */
    public ArmoryBuilding getBuildingInfo(GetArmoryBuildingInfoCommand command) {
        var kosProfileId = command.getKosProfileId();
        var numberOfWeaponSet = weaponSetRepository.countByKosProfileId(kosProfileId);
        var building = armoryBuildingRepository.findByKosProfileId(kosProfileId)
                                               .orElseThrow(() -> KOSException.of(ErrorCode.ARMORY_BUILDING_IS_NOT_FOUND));
        building.validUnlockBuilding(technologyService);
        return building.setNumberOfWeaponSet(numberOfWeaponSet);
    }

    @Override
    protected BaseBuildingConfig getBuildingConfig(Long level) {
        return buildingConfigDataSource.getConfig(BuildingName.ARMORY, level);
    }
}
