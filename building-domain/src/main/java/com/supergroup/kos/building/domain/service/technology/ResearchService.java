package com.supergroup.kos.building.domain.service.technology;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.async.ResearchServiceAsyncTask;
import com.supergroup.kos.building.domain.command.ResearchCommand;
import com.supergroup.kos.building.domain.constant.TechnologyType;
import com.supergroup.kos.building.domain.model.mining.ResearchBuilding;
import com.supergroup.kos.building.domain.model.point.Point;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.research.ResearchResult;
import com.supergroup.kos.building.domain.model.technology.Technology;
import com.supergroup.kos.building.domain.model.technology.UserTechnology;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.point.PointRepository;
import com.supergroup.kos.building.domain.repository.persistence.technology.UserTechnologyRepository;
import com.supergroup.kos.building.domain.service.building.ArmoryBuildingService;
import com.supergroup.kos.building.domain.service.building.CommandBuildingService;
import com.supergroup.kos.building.domain.service.building.CommunityBuildingService;
import com.supergroup.kos.building.domain.service.building.ScoutBuildingService;
import com.supergroup.kos.building.domain.service.building.StorageBuildingService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.ship.EscortShipService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResearchService {

    private final StorageBuildingService   storageBuildingService;
    private final BuildingRepository       buildingRepository;
    private final KosProfileService        kosProfileService;
    private final CommandBuildingService   commandBuildingService;
    private final ArmoryBuildingService    armoryBuildingService;
    private final CommunityBuildingService communityBuildingService;
    private final ScoutBuildingService     scoutBuildingService;
    private final UserTechnologyRepository userTechnologyRepository;
    private final PointRepository          pointRepository;
    private final EscortShipService        escortShipService;
    private final ResearchServiceAsyncTask researchServiceAsyncTask;

    @Transactional
    public List<ResearchResult> research(ResearchCommand command) {
        if (checkRequirement(command.getResearchBuilding(), command.getUserTechnology(), command.getPoint())
            && checkConditionItems(command.getResearchBuilding().getKosProfile().getId(), command.getUserTechnology().getTechnology())) {
            takeRequirement(command.getUserTechnology(), command.getPoint());
            return executeResearch(command.getUserTechnology());
        }
        throw KOSException.of(ErrorCode.DO_NOT_MEET_RESOURCE_REQUIREMENT);
    }

    /**
     * Set technology is researched
     */
    private List<ResearchResult> executeResearch(UserTechnology userTechnology) {
        // execute effect technology
        var handler = createHandler(userTechnology.getTechnology().getTechnologyType());
        var res = handler.research(userTechnology);
        // unlock item
        userTechnology.setIsResearched(true);
        userTechnology.setIsLock(false);
        // save
        userTechnologyRepository.save(userTechnology);
        KosProfile kosProfile = kosProfileService.getKosProfileById(userTechnology.getResearchBuilding().getKosProfile().getId());
        researchServiceAsyncTask.sendNotification(userTechnology.getTechnology().getName(), kosProfile.getUser().getId());
        return res;
    }

    /**
     * Take requirement to research
     */
    private void takeRequirement(UserTechnology userTechnology, Point point) {
        point.setTpPoint(point.getTpPoint() - userTechnology.getTechnology().getTechPoint());
        // save point
        pointRepository.save(point);
    }

    private boolean checkConditionItems(Long kosProfileId, Technology technology) {
        return technology.getConditions().stream().allMatch(t -> {
            var ut = userTechnologyRepository.findByKosProfileIdAndTechnologyCode(kosProfileId, t.getCode())
                                             .orElseThrow(() -> KOSException.of(ErrorCode.USER_TECHNOLOGY_NOT_FOUND));
            return ut.getIsResearched();
        });
    }

    /**
     * check requirement tp point, technology is researchable
     */
    private boolean checkRequirement(ResearchBuilding researchBuilding, UserTechnology userTechnology, Point point) {
        return point.getTpPoint() >= userTechnology.getTechnology().getTechPoint()
               && !userTechnology.getIsResearched()
               && userTechnology.getTechnology().getLevelBuilding() <= researchBuilding.getLevel();
    }

    private ResearchHandler createHandler(TechnologyType type) {
        switch (type) {
            case SCIENCE:
                return new ResearchScienceHandler(
                        buildingRepository,
                        kosProfileService,
                        armoryBuildingService,
                        communityBuildingService,
                        scoutBuildingService
                );
            case ECONOMY:
                return new ResearchEconomyHandler(
                        buildingRepository,
                        kosProfileService
                );
            case MILITARY:
                return new ResearchMilitaryHandler(
                        commandBuildingService,
                        escortShipService
                );
            case ADVANCE_MILITARY:
                return new ResearchAdvanceMilitaryHandler(
                        commandBuildingService,
                        escortShipService
                );
            default:
                throw KOSException.of(ErrorCode.TECHNOLOGY_INVALID);
        }
    }
}
