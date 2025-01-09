package com.supergroup.kos.api.ship;

import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.asset.service.AssetService;
import com.supergroup.kos.building.domain.command.UpgradeEscortShipGroupCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.ship.EscortShipGroupService;
import com.supergroup.kos.mapper.EscortShipGroupMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/escort-ship-group")
@RequiredArgsConstructor
public class EscortShipGroupRestController {

    private final EscortShipGroupService escortShipGroupService;
    private final AssetService           assetService;
    private final EscortShipGroupMapper  escortShipGroupMapper;
    private final KosProfileService      kosProfileService;

    @GetMapping("")
    public ResponseEntity<?> getEscortShipGroups() {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        var escortShipGroups = escortShipGroupService.getEscortShipGroups(kosProfileId);
        var responses = escortShipGroups.stream().map(group -> {
            var response = escortShipGroupMapper.toDTO(group);
            var thumbnail = assetService.getUrl(group.getEscortShipGroupLevelConfig().getEscortShipGroupConfig().getThumbnail());
            response.setThumbnail(thumbnail);
            return response;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/upgrade")
    public ResponseEntity<?> upgrade(@Valid @RequestBody UpgradeEscortShipGroupCommand command) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        escortShipGroupService.upgrade(command.setKosProfileId(kosProfileId));
        return ResponseEntity.accepted().build();
    }
}
