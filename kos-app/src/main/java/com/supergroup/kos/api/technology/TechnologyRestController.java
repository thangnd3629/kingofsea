package com.supergroup.kos.api.technology;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.kos.building.domain.command.GetAllTechnologyCommand;
import com.supergroup.kos.building.domain.command.GetResearchBuildingInfo;
import com.supergroup.kos.building.domain.command.GetTechnologyCommand;
import com.supergroup.kos.building.domain.command.ResearchCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.TechnologyCode;
import com.supergroup.kos.building.domain.constant.TechnologyType;
import com.supergroup.kos.building.domain.exception.TechRequirementException;
import com.supergroup.kos.building.domain.model.technology.Technology;
import com.supergroup.kos.building.domain.service.building.ResearchBuildingService;
import com.supergroup.kos.building.domain.service.point.PointService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.technology.ResearchService;
import com.supergroup.kos.building.domain.service.technology.TechnologyService;
import com.supergroup.kos.dto.RequirementDTO;
import com.supergroup.kos.dto.technology.TechnologyTreeItem;
import com.supergroup.kos.dto.technology.TechnologyTreeResponse;
import com.supergroup.kos.mapper.ResearchTechnologyResultMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/technology")
@RequiredArgsConstructor
public class TechnologyRestController {

    private final ResearchBuildingService        researchBuildingService;
    private final KosProfileService              kosProfileService;
    private final PointService                   pointService;
    private final ResearchService                researchService;
    private final ResearchTechnologyResultMapper researchTechnologyResultMapper;
    private final TechnologyService              technologyService;

    @GetMapping
    public ResponseEntity<TechnologyTreeResponse> getTechnology(@RequestParam("type") @Valid @NotEmpty @NotBlank String type) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));

        if (TechnologyType.valueOf(type).equals(TechnologyType.MILITARY)
            || TechnologyType.valueOf(type).equals(TechnologyType.ADVANCE_MILITARY)) {
            var techRequired = technologyService.findTechnologyUnlockMilitaryAndAdvancedTech();
            if (!kosProfile.getIsUnlockMilitaryTech() || !kosProfile.getIsUnlockAdvancedMilitaryTech()) {
                throw new TechRequirementException(ErrorCode.TECHNOLOGY_TYPE_IS_LOCKED, techRequired);
            }
        }

        var researchBuilding = researchBuildingService.getBuildingInfo(new GetResearchBuildingInfo(kosProfile.getId()));
        var userTechnologies = researchBuildingService.getAllTechnology(
                new GetAllTechnologyCommand(kosProfile.getId(), TechnologyType.valueOf(type)));
        var tree = new ArrayList<TechnologyTreeItem>();
        userTechnologies.forEach(t -> {
            tree.add(new TechnologyTreeItem().setName(t.getTechnology().getName())
                                             .setCode(t.getTechnology().getCode())
                                             .setIsResearched(Objects.nonNull(t.getIsResearched()) && t.getIsResearched())
                                             .setIsResearchable(!t.getIsResearched() && t.getTechnology().getLevelBuilding() <= researchBuilding.getLevel())
                                             .setIsLock(t.getIsLock())
                                             .setRequirement(new RequirementDTO().setLevelBuilding(t.getTechnology().getLevelBuilding())
                                                                                 .setTechPoint(t.getTechnology().getTechPoint()))
                                             .setId(t.getTechnology().getCode().toString())
                                             .setConditions(t.getTechnology().getConditions().stream().map(Technology::getCode).collect(Collectors.toList()))
                                             .setEffect(t.getTechnology().getDescription()));
        });
        var response = new TechnologyTreeResponse().setTree(tree);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/item")
    public ResponseEntity<TechnologyTreeItem> getItemDetail(@RequestParam("code") @Valid @NotEmpty @NotBlank TechnologyCode code) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var item = researchBuildingService.getTechnologyDetail(new GetTechnologyCommand(kosProfile.getId(), code));
        return ResponseEntity.ok(new TechnologyTreeItem().setName(item.getTechnology().getName())
                                                         .setCode(item.getTechnology().getCode())
                                                         .setIsLock(item.getIsLock())
                                                         .setIsResearched(Objects.nonNull(item.getIsResearched()) && item.getIsResearched())
                                                         .setIsResearchable(!item.getIsResearched() && !item.getIsLock())
                                                         .setRequirement(new RequirementDTO().setLevelBuilding(item.getTechnology().getLevelBuilding()))
                                                         .setId(item.getTechnology().getCode().toString())
                                                         .setConditions(item.getTechnology().getConditions().stream().map(Technology::getCode)
                                                                  .collect(Collectors.toList()))
                                                         .setEffect(item.getTechnology().getDescription()));
    }

    @PostMapping("/item")
    public ResponseEntity<?> research(@RequestParam("code") @Valid @NotEmpty @NotBlank TechnologyCode code) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var researchBuilding = researchBuildingService.getBuildingInfo(new GetResearchBuildingInfo(kosProfile.getId()));
        var userTechnology = researchBuildingService.getTechnologyDetail(new GetTechnologyCommand(kosProfile.getId(), code));
        var point = pointService.getKosPoint(kosProfile);
        var result = researchService.research(new ResearchCommand(researchBuilding, userTechnology, point));
        return ResponseEntity.ok(researchTechnologyResultMapper.toResponse(result));
    }
}
