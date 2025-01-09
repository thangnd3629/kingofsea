package com.supergroup.kos.building.domain.service.building;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetCommandBuildingInfo;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.building.CommandBuilding;
import com.supergroup.kos.building.domain.model.config.BaseBuildingConfig;
import com.supergroup.kos.building.domain.model.config.CommandBuildingConfig;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.building.CommandBuildingRepository;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.technology.TechnologyService;

@Service
public class CommandBuildingService extends BaseBuildingService {

    private final CommandBuildingRepository commandBuildingRepository;
    private final BuildingConfigDataSource  buildingConfigDataSource;
    private final TechnologyService         technologyService;

    public CommandBuildingService(@Autowired CommandBuildingRepository commandBuildingRepository,
                                  @Autowired BuildingConfigDataSource buildingConfigDataSource,
                                  @Autowired TechnologyService technologyService,
                                  @Autowired KosProfileService kosProfileService) {
        super(kosProfileService, buildingConfigDataSource);
        this.commandBuildingRepository = commandBuildingRepository;
        this.buildingConfigDataSource = buildingConfigDataSource;
        this.technologyService = technologyService;
    }

    public CommandBuilding getBuildingInfo(GetCommandBuildingInfo command) {
        var commandBuilding = commandBuildingRepository.findByKosProfileId(command.getKosProfileId())
                                                       .orElseThrow(() -> KOSException.of(ErrorCode.COMMAND_BUILDING_IS_NOT_FOUND));

        commandBuilding.validUnlockBuilding(technologyService);

        var commandBuildingConfig = (CommandBuildingConfig) buildingConfigDataSource.getConfig(BuildingName.COMMAND,
                                                                                               commandBuilding.getLevel());
        commandBuilding.setSlotMotherShip(commandBuildingConfig.getSlotMotherShip());
        return commandBuilding;
    }

    @Override
    protected BaseBuildingConfig getBuildingConfig(Long level) {
        return buildingConfigDataSource.getConfig(BuildingName.COMMAND, level);
    }

    public CommandBuilding save(CommandBuilding commandBuilding) {
        return commandBuildingRepository.save(commandBuilding);
    }
}
