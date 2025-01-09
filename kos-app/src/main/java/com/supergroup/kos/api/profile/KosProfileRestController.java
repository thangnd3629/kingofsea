package com.supergroup.kos.api.profile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/user/kos/profile")
@RequiredArgsConstructor
public class KosProfileRestController {
    private final KosProfileService kosProfileService;

    @PostMapping("/init")
    public ResponseEntity<?> init() {
        kosProfileService.createNewProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        return ResponseEntity.ok().build();
    }
}
