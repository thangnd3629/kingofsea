package com.supergroup.admin.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.kos.building.domain.command.GetQueenBuildingInfo;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.repository.persistence.queen.QueenRepository;
import com.supergroup.kos.building.domain.service.building.QueenBuildingService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/user/{userId}/queen")
@RequiredArgsConstructor
public class QueenRestController {
    private final KosProfileService    kosProfileService;
    private final QueenRepository      queenRepository;
    private final QueenBuildingService queenBuildingService;

    @DeleteMapping("")
    public ResponseEntity<?> deleteAllQueen(@PathVariable("userId") Long userId) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(userId)).getId();
        var queenBuilding = queenBuildingService.getBuildingInfo(new GetQueenBuildingInfo(kosProfileId));
        queenRepository.deleteByQueenBuildingId(queenBuilding.getId());
        return ResponseEntity.noContent().build();
    }

}
