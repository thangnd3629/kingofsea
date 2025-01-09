package com.supergroup.admin.api;

import javax.validation.Valid;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.admin.domain.command.AdminCreateEscortShipCommand;
import com.supergroup.admin.domain.service.AdminEscortShipService;
import com.supergroup.admin.dto.AdminRewardEscortShipRequest;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/escort-ship")
@RequiredArgsConstructor
public class EscortShipRestController {

    private final KosProfileService kosProfileService;

    private final AdminEscortShipService adminEscortShipService;

    @PostMapping("/{userId}")
    public ResponseEntity<?> rewardEscortShip(@PathVariable Long userId, @Valid @RequestBody AdminRewardEscortShipRequest request) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(userId)).getId();
        adminEscortShipService.createEscortShip(
                new AdminCreateEscortShipCommand().setModelId(request.getModelId()).setKosProfileId(kosProfileId));
        return ResponseEntity.status(HttpStatus.SC_CREATED).build();
    }

}
