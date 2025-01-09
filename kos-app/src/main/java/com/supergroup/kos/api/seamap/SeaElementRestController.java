package com.supergroup.kos.api.seamap;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;
import com.supergroup.kos.building.domain.service.seamap.activity.LineUpService;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;
import com.supergroup.kos.dto.battle.InvaderResponse;
import com.supergroup.kos.dto.seamap.activity.SeaActivityDTO;
import com.supergroup.kos.dto.seamap.elements.ElementResponse;
import com.supergroup.kos.mapper.battle.InvaderMapper;
import com.supergroup.kos.mapper.elements.ElementMapper;
import com.supergroup.kos.mapper.seamap.activity.LineUpMapper;
import com.supergroup.kos.mapper.seamap.activity.SeaActivityMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/seamap/element")
@RequiredArgsConstructor
public class SeaElementRestController {
    private final SeaElementService  seaElementService;
    private final ElementMapper      elementMapper;
    private final InvaderMapper      invaderMapper;
    private final SeaActivityService seaActivityService;
    private final KosProfileService  kosProfileService;
    private final SeaActivityMapper  seaActivityMapper;
    private final LineUpMapper       lineUpMapper;
    private final LineUpService      lineUpService;

    @GetMapping("/{id}")
    public ResponseEntity<ElementResponse> getMap(@PathVariable("id") Long id) {
        return ResponseEntity.ok(elementMapper.map(seaElementService.getElementById(id)));
    }

    @GetMapping("/{id}/activity")
    public ResponseEntity<List<SeaActivityDTO>> getActivityOnBase(@PathVariable("id") Long id) {
        KosProfile kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var activities = seaActivityService.findByElementIdAndKosProfileId(id, kosProfile.getId());
        return ResponseEntity.ok(seaActivityMapper.toDtos(activities, lineUpMapper, seaElementService, lineUpService));
    }

    @GetMapping("/{id}/invader")
    public ResponseEntity<InvaderResponse> invaderInfo(@PathVariable("id") Long id) {
        var invader = seaElementService.invader(id);
        invader.setActivitiesOnOccupiedBase(seaActivityService.findByElementIdAndKosProfileId(id, invader.getKosProfileInvader().getId()));
        return ResponseEntity.ok(invaderMapper.map(invader));
    }
}
