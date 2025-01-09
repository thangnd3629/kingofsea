package com.supergroup.kos.api.point;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.ConvertGP2TPCommand;
import com.supergroup.kos.building.domain.command.GetResearchBuildingInfo;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.service.building.CastleBuildingService;
import com.supergroup.kos.building.domain.service.building.ResearchBuildingService;
import com.supergroup.kos.building.domain.service.point.PointService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.dto.point.ConvertPointRequest;
import com.supergroup.kos.dto.point.ConvertPointResponse;
import com.supergroup.kos.dto.point.PointResponse;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RequestMapping("/v1/point")
@RestController
@RequiredArgsConstructor
public class PointRestController {

    private final PointService            pointService;
    private final KosProfileService       kosProfileService;
    private final ResearchBuildingService researchBuildingService;
    private final CastleBuildingService   castleBuildingService;

    @PostMapping("/glory/convert/tech-point")
    public ResponseEntity<ConvertPointResponse> convertGP2TP(@RequestBody ConvertPointRequest request) {
        var kosProfile = kosProfileService.findByUserId(AuthUtil.getUserId())
                                          .orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND));
        var point = pointService.findByKosProfile_Id(kosProfile.getId())
                                .orElseThrow(() -> KOSException.of(ErrorCode.KOS_POINTS_NOT_FOUND));
        var researchBuilding = researchBuildingService.getBuildingInfo(new GetResearchBuildingInfo(kosProfile.getId()));
        var amount = pointService.convertGloryPointToTechPoint(new ConvertGP2TPCommand(point, researchBuilding, request.getAmount()));
        return ResponseEntity.ok(new ConvertPointResponse(amount));
    }

    @GetMapping("")
    public ResponseEntity<PointResponse> getPoint() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var kosProfileId = kosProfile.getId();
        var point = pointService.getKosPoint(kosProfile);
        var castleBuilding = castleBuildingService.getCastleBuildingDetail(new KosProfileCommand().setKosProfileId(kosProfileId));
        var response = new PointResponse()
                .setGp(point.getGpPoint())
                .setTp(point.getTpPoint())
                .setMp(point.getMpPoint())
                .setMpMultiplier(castleBuilding.getMpMultiplier());
        return ResponseEntity.ok(response);
    }
}
