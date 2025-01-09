package com.supergroup.kos.api.building;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.kos.building.domain.command.GetAllTechnologyCommand;
import com.supergroup.kos.building.domain.command.GetAllUpgradeInfoCommand;
import com.supergroup.kos.building.domain.command.GetResearchBuildingInfo;
import com.supergroup.kos.building.domain.command.GetUpgradeInfoCommand;
import com.supergroup.kos.building.domain.command.GetUpgradeStatusCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.UpgradeBuildingCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.TechnologyType;
import com.supergroup.kos.building.domain.model.config.ResearchBuildingConfig;
import com.supergroup.kos.building.domain.model.mining.ResearchBuilding;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.technology.Technology;
import com.supergroup.kos.building.domain.model.technology.UserTechnology;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.ResearchBuildingService;
import com.supergroup.kos.building.domain.service.point.PointService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.technology.TechnologyService;
import com.supergroup.kos.building.domain.service.upgrade.UpgradeService;
import com.supergroup.kos.dto.RequirementDTO;
import com.supergroup.kos.dto.building.ResearchBuildingInfoResponse;
import com.supergroup.kos.dto.technology.TechnologyResponse;
import com.supergroup.kos.dto.technology.TechnologyTreeItem;
import com.supergroup.kos.dto.technology.TechnologyTypeResponse;
import com.supergroup.kos.dto.upgrade.UpgradeResearchBuildingInfoResponse;
import com.supergroup.kos.dto.upgrade.UpgradeReward;
import com.supergroup.kos.dto.upgrade.UpgradeStatusResponse;
import com.supergroup.kos.mapper.UpgradeSessionMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/building/research")
@RequiredArgsConstructor
public class ResearchBuildingRestController {

    private final ResearchBuildingService researchBuildingService;
    private final UpgradeService          upgradeService;
    private final AssetsService           assetsService;
    private final KosProfileService       kosProfileService;
    private final PointService            pointService;
    private final UpgradeSessionMapper    upgradeSessionMapper;
    private final TechnologyService       technologyService;

    @GetMapping("")
    public ResponseEntity<ResearchBuildingInfoResponse> getBuildingInfo() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var researchBuilding = researchBuildingService.getBuildingInfo(new GetResearchBuildingInfo(kosProfile.getId()));
        var point = pointService.getKosPoint(kosProfile);

        var technologyItems = Arrays.stream(TechnologyType.values())
                                    .map(technologyType -> mapToTechnologyTypeResponse(kosProfile, researchBuilding, technologyType))
                                    .collect(Collectors.toList());

        var response = new ResearchBuildingInfoResponse().setLevel(researchBuilding.getLevel())
                                                         .setGloryPoint(point.getGpPoint())
                                                         .setTechPoint(point.getTpPoint())
                                                         .setTechnology(technologyItems);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/upgrade")
    public ResponseEntity<?> getUpgradeInfo(@RequestParam(value = "level", required = false) Long level) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        if (Objects.isNull(level)) {
            var command = new GetAllUpgradeInfoCommand(BuildingName.RESEARCH, kosProfile.getId());
            var upgradeInfos = (List<ResearchBuildingConfig>) researchBuildingService.getAllUpgradeInfo(command);
            var listInfoResponse = upgradeInfos.stream()
                                               .map(this::mapToUpgradeResearchBuildingInfoResponse)
                                               .collect(Collectors.toList());
            return ResponseEntity.ok(Map.of("details", listInfoResponse));
        } else {
            var upgradeInfo = (ResearchBuildingConfig) researchBuildingService.getUpgradeInfo(new GetUpgradeInfoCommand(level, kosProfile.getId()));
            return ResponseEntity.ok(mapToUpgradeResearchBuildingInfoResponse(upgradeInfo));
        }
    }

    @PostMapping("/upgrade")
    public ResponseEntity<?> upgradeBuilding() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var researchBuilding = researchBuildingService.getBuildingInfo(new GetResearchBuildingInfo(kosProfile.getId()));
        var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
        upgradeService.upgrade(new UpgradeBuildingCommand(kosProfile, researchBuilding, asset));
        return ResponseEntity.ok().build();
    }

    @GetMapping("upgrade/status")
    public ResponseEntity<UpgradeStatusResponse> getUpgradeStatus() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        UpgradeSession upgradeSession = upgradeService.getUpgradeSession(new GetUpgradeStatusCommand().setKosProfileId(kosProfile.getId())
                                                                                                      .setBuildingName(BuildingName.RESEARCH));
        return ResponseEntity.ok(upgradeSessionMapper.toUpgradeStatusResponse(upgradeSession));
    }

    private TechnologyTypeResponse mapToTechnologyTypeResponse(KosProfile kosProfile,
                                                               ResearchBuilding researchBuilding,
                                                               TechnologyType technologyType) {
        var tree = researchBuildingService.getAllTechnology(new GetAllTechnologyCommand(kosProfile.getId(), technologyType));
        var numResearchable = tree.stream()
                                  .filter(t -> !t.getIsResearched() && t.getTechnology().getLevelBuilding() <= researchBuilding.getLevel())
                                  .count();
        var numResearched = tree.stream()
                                .filter(UserTechnology::getIsResearched)
                                .count();

        // ======== BAD CODE :( =========
        // TODO refactor it, pls
        Boolean isLock;
        switch (technologyType) {
            case MILITARY:
                isLock = !kosProfile.getIsUnlockMilitaryTech();
                break;
            case ADVANCE_MILITARY:
                isLock = !kosProfile.getIsUnlockAdvancedMilitaryTech();
                break;
            default:
                isLock = false;
                break;
        }

        var response = new TechnologyTypeResponse().setTotalItem((long) tree.size())
                                                   .setNumResearchable(numResearchable)
                                                   .setIsLock(isLock)
                                                   .setNumResearchedItem(numResearched)
                                                   .setName(technologyType.name());

        // get tech required information
        Technology techRequired = null;
        if (technologyType.equals(TechnologyType.MILITARY) || technologyType.equals(TechnologyType.ADVANCE_MILITARY)) {
            techRequired = technologyService.findTechnologyUnlockMilitaryAndAdvancedTech();
        }

        if (Objects.nonNull(techRequired)) {
            response.setTechRequired(new TechnologyResponse().setCode(techRequired.getCode())
                                                             .setType(techRequired.getTechnologyType())
                                                             .setName(techRequired.getName()));
        }
        return response;
    }

    private UpgradeResearchBuildingInfoResponse mapToUpgradeResearchBuildingInfoResponse(ResearchBuildingConfig config) {
        return new UpgradeResearchBuildingInfoResponse().setDuration(config.getUpgradeDuration())
                                                        .setRequirement(new RequirementDTO().setGold(config.getGold())
                                                                                            .setStone(config.getStone())
                                                                                            .setWood(config.getWood()))
                                                        .setLevel(config.getLevel())
                                                        .setConvertRate(config.getConvertRate())
                                                        .setUnlock(config.getUnlockTechnologies().stream()
                                                                         .map(t -> new TechnologyTreeItem()
                                                                                 .setCode(t.getCode())
                                                                                 .setName(t.getName()))
                                                                         .collect(Collectors.toList()))
                                                        .setReward(new UpgradeReward().setGloryPoint(
                                                                config.getGpPointReward()));
    }

}
