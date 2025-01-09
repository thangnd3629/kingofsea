package com.supergroup.kos.api.town;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.asset.service.AssetService;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;
import com.supergroup.kos.dto.town.OccupationBaseDTO;
import com.supergroup.kos.mapper.town.OccupationBaseMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/town/occupations")
@RequiredArgsConstructor
public class TownRestController {
    private final KosProfileService    kosProfileService;
    private final UserBaseService      userBaseService;
    private final OccupationBaseMapper occupationBaseMapper;
    private final AssetService         assetService;

    @GetMapping("")
    public ResponseEntity<List<OccupationBaseDTO>> getListOccupations() {
        KosProfile kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var baseOccupies = userBaseService.getListOccupations(kosProfile.getId());
        return ResponseEntity.ok(occupationBaseMapper.toOccupationBaseDTOs(baseOccupies, kosProfile.getId()));
    }
}
