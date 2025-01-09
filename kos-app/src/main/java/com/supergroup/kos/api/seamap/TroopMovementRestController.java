package com.supergroup.kos.api.seamap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.seamap.activity.MoveSessionService;
import com.supergroup.kos.dto.PageResponse;
import com.supergroup.kos.dto.seamap.activity.TroopMovementDTO;
import com.supergroup.kos.mapper.seamap.activity.TroopMovementMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@RequestMapping("/v1/seamap/movement")
public class TroopMovementRestController {
    private final MoveSessionService  moveSessionService;
    private final KosProfileService   kosProfileService;
    private final TroopMovementMapper troopMovementMapper;

    @GetMapping("/")
    public ResponseEntity<?> getTroopMovement(HttpServletRequest request) {
        Pageable pageable = (Pageable) request.getAttribute("pageable");
        KosProfile kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        Page<MoveSession> pageResult = moveSessionService.getTroopMovement(kosProfile.getId(), pageable);
        PageResponse<TroopMovementDTO> result = new PageResponse<>();
        result.setData(
                troopMovementMapper.toDtos(pageResult.toList())
                      );
        result.setTotal((long) pageResult.getNumberOfElements());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTroopMovement(@PathVariable(name = "id") Long moveSessionId) {
        KosProfile kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        MoveSession deletedMoveSession = moveSessionService.deleteMoveSession(kosProfile.getId(), moveSessionId);
        return ResponseEntity.ok(troopMovementMapper.toDto(deletedMoveSession));

    }
}
