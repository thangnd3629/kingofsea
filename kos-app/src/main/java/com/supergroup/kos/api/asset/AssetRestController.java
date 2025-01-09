package com.supergroup.kos.api.asset;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.model.building.CastleBuilding;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.CastleBuildingService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.dto.asset.KosAssetsResponse;
import com.supergroup.kos.dto.asset.PeopleInfoResponse;
import com.supergroup.kos.mapper.KosAssetsMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/user/kos/asset")
@RequiredArgsConstructor
public class AssetRestController {

    private final AssetsService         assetsService;
    private final KosProfileService     kosProfileService;
    private final KosAssetsMapper       kosAssetsMapper;
    private final CastleBuildingService castleBuildingService;

    @GetMapping("/assets")
    public ResponseEntity<KosAssetsResponse> getAssets() {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        var command = new KosProfileCommand().setKosProfileId(kosProfileId);
        CastleBuilding castleBuilding = castleBuildingService.getCastleBuilding(new KosProfileCommand().setKosProfileId(kosProfileId));
        KosAssetsResponse kosAssetsResponse = kosAssetsMapper.toDTO(assetsService.getAssets(command));
        kosAssetsResponse.setIdlePeople(castleBuilding.getIdlePeople().longValue());
        return ResponseEntity.ok(kosAssetsResponse);
    }

    @GetMapping("/people")
    public ResponseEntity<PeopleInfoResponse> getPeopleInfo() {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        var totalPeople = assetsService.getTotalPeople(kosProfileId).getTotalPeople();
        var castleBuilding = castleBuildingService.getCastleBuilding(new KosProfileCommand().setKosProfileId(kosProfileId));
        return ResponseEntity.ok(new PeopleInfoResponse().setIdle(castleBuilding.getIdlePeople().longValue())
                                                         .setTotal(totalPeople.longValue()));
    }


}
