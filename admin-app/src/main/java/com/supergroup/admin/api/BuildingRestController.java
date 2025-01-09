package com.supergroup.admin.api;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.admin.domain.command.AdminUpdateBuildingCommand;
import com.supergroup.admin.domain.dto.request.AdminUpdateBuildingRequest;
import com.supergroup.admin.domain.service.AdminBuildingService;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/user/{userId}/base-building")
@RequiredArgsConstructor
public class BuildingRestController {

    private final AdminBuildingService adminBuildingService;
    private final KosProfileService    kosProfileService;

    @PutMapping("/{buildingName}")
    public ResponseEntity<?> updateBuilding(@PathVariable("buildingName") String buildingName, @PathVariable Long userId, @Valid @RequestBody
    AdminUpdateBuildingRequest request) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(userId)).getId();
        adminBuildingService.updateBuilding(
                new AdminUpdateBuildingCommand()
                        .setLevel(request.getLevel())
                        .setBuildingName(BuildingName.valueOf(buildingName.toUpperCase()))
                        .setKosProfileId(kosProfileId));
        return ResponseEntity.accepted().build();
    }
}
