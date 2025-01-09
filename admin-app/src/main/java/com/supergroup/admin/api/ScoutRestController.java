package com.supergroup.admin.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetScoutBuildingInfoCommand;
import com.supergroup.kos.building.domain.model.mining.ScoutBuilding;
import com.supergroup.kos.building.domain.service.building.ScoutBuildingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/scout")
@RequiredArgsConstructor
public class ScoutRestController {
    private final ScoutBuildingService scoutBuildingService;

    @PostMapping
    public ResponseEntity<?> updateSolider(@RequestParam(name = "numberSolider") Long numberSolider,
                                           @RequestParam(name = "kosProfileId") Long kosProfileId) {
        ScoutBuilding scoutBuilding = scoutBuildingService.getBuildingInfo(new GetScoutBuildingInfoCommand(kosProfileId));
        Long soliderMission = scoutBuilding.getTotalScout() - scoutBuilding.getAvailableScout();
        if (soliderMission < 0 || soliderMission < numberSolider) {
            throw KOSException.of(ErrorCode.BAD_REQUEST_ERROR);
        }
        scoutBuilding.setTotalScout(soliderMission + numberSolider)
                     .setAvailableScout(scoutBuilding.getTotalScout() - soliderMission);
        scoutBuildingService.save(scoutBuilding);
        return ResponseEntity.ok().build();
    }
}
