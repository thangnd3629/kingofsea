package com.supergroup.admin.api;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.admin.domain.command.AdminUpdateCommandBuildingCommand;
import com.supergroup.admin.domain.service.AdminCommandBuildingService;
import com.supergroup.admin.dto.request.AdminUpdateCommandBuildingRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/kos-profile/{kosProfileId}/building/command")
@RequiredArgsConstructor
public class AdminCommandBuildingRestController {

    private final AdminCommandBuildingService commandBuildingService;

    @PatchMapping("")
    public ResponseEntity<?> updateBuilding(@PathVariable Long kosProfileId, @Valid @RequestBody
    AdminUpdateCommandBuildingRequest request) {
        commandBuildingService.updateBuilding(
                new AdminUpdateCommandBuildingCommand()
                        .setMaxSlotWeaponOfMotherShip(request.getMaxSlotWeaponOfMotherShip())
                        .setLevel(request.getLevel())
                        .setKosProfileId(kosProfileId));
        return ResponseEntity.accepted().build();
    }
}
