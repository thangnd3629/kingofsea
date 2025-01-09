package com.supergroup.admin.api;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.admin.domain.command.AdminUpdateCommunityBuildingCommand;
import com.supergroup.admin.domain.service.AdminCommunityBuildingService;
import com.supergroup.admin.dto.AdminUpdateCommunityBuildingRequest;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/community")
@RequiredArgsConstructor
public class CommunityRestController {

    private final KosProfileService             kosProfileService;
    private final AdminCommunityBuildingService adminCommunityBuildingService;

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateBuilding(@PathVariable Long userId, @Valid @RequestBody
    AdminUpdateCommunityBuildingRequest request) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(userId)).getId();
        adminCommunityBuildingService.updateBuilding(
                new AdminUpdateCommunityBuildingCommand()
                        .setMaxListingRelic(request.getMaxListingRelic())
                        .setKosProfileId(kosProfileId));
        return ResponseEntity.accepted().build();
    }

}
