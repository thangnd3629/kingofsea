package com.supergroup.kos.api.queen;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.asset.service.AssetService;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.queen.QueenService;
import com.supergroup.kos.dto.queen.QueenResponse;
import com.supergroup.kos.mapper.QueenMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/queen")
@RequiredArgsConstructor
public class QueenRestController {
    private final KosProfileService kosProfileService;
    private final QueenService      queenService;
    private final AssetService      assetService;
    private final QueenMapper       queenMapper;

    @GetMapping("")
    public ResponseEntity<List<QueenResponse>> getQueens() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var queens = queenService.getQueens(kosProfile.getId());
        var response = queens.stream().map(queen -> {
            var thumbnail = assetService.getUrl(queen.getQueenConfig().getThumbnail());
            var res = queenMapper.toDTO(queen);
            res.getModel().setThumbnail(thumbnail);
            return res;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QueenResponse> getQueenById(@PathVariable("id") Long queenId) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var queen = queenService.getQueenByIdAndKosProfileId(queenId, kosProfile.getId());
        var thumbnail = assetService.getUrl(queen.getQueenConfig().getThumbnail());
        queen.setQueenConfig(queen.getQueenConfig().setThumbnail(thumbnail));
        return ResponseEntity.ok(queenMapper.toDTO(queen));
    }

}


