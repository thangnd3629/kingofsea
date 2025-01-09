package com.supergroup.admin.api;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.admin.domain.command.AdminUpdateUserTechnologyCommand;
import com.supergroup.admin.domain.service.AdminUserTechnologyService;
import com.supergroup.admin.dto.AdminUpdateUserTechnologyRequest;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.TechnologyCode;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/user/{userId}/technology")
@RequiredArgsConstructor
public class AdminUserTechnologyRestController {
    private final KosProfileService kosProfileService;
    private final AdminUserTechnologyService adminUserTechnologyService;
    @PutMapping("/{technologyCode}")
    public ResponseEntity<?> updateTechnology(@PathVariable Long userId, @PathVariable TechnologyCode technologyCode, @Valid @RequestBody
    AdminUpdateUserTechnologyRequest request) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(userId)).getId();
        adminUserTechnologyService.updateUserTechnology(
                new AdminUpdateUserTechnologyCommand()
                        .setKosProfileId(kosProfileId)
                        .setTechnologyCode(technologyCode)
                        .setIsLock(request.getIsLock())
                        .setIsResearched(request.getIsResearched()));
        return ResponseEntity.accepted().build();
    }

}
