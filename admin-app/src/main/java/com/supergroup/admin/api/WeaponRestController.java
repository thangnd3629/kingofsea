package com.supergroup.admin.api;

import javax.validation.Valid;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.admin.domain.command.AdminCreateWeaponCommand;
import com.supergroup.admin.domain.service.AdminWeaponService;
import com.supergroup.admin.dto.AdminRewardWeaponRequest;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/weapon")
@RequiredArgsConstructor
public class WeaponRestController {


    private final KosProfileService kosProfileService;

    private final AdminWeaponService adminWeaponService;


    @PostMapping("/{userId}")
    public ResponseEntity<?> rewardWeapon(@PathVariable Long userId, @Valid @RequestBody AdminRewardWeaponRequest request) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(userId)).getId();
        adminWeaponService.createWeapon(
                new AdminCreateWeaponCommand().setModelId(request.getModelId()).setKosProfileId(kosProfileId));
        return ResponseEntity.status(HttpStatus.SC_CREATED).build();
    }

}
