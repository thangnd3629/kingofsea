package com.supergroup.admin.api;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.admin.domain.command.AdminCreateRelicCommand;
import com.supergroup.admin.domain.service.AdminRelicService;
import com.supergroup.admin.dto.AdminRewardRelicRequest;
import com.supergroup.admin.dto.RelicResponse;
import com.supergroup.admin.mapper.RelicMapper;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/relic")
@RequiredArgsConstructor
public class RelicRestController {
    private final AdminRelicService adminRelicService;
    private final KosProfileService kosProfileService;
    private final RelicMapper       relicMapper;

    @PostMapping("/{userId}")
    public ResponseEntity<RelicResponse> rewardRelic(@PathVariable Long userId, @Valid @RequestBody AdminRewardRelicRequest request) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(userId)).getId();
        var relic = adminRelicService.createRelic(
                new AdminCreateRelicCommand().setRelicModelId(request.getRelicModelId()).setKosProfileId(kosProfileId));
        return ResponseEntity.ok(relicMapper.toDTO(relic));
    }
}
