package com.supergroup.kos.api.building;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.asset.service.AssetService;
import com.supergroup.kos.building.domain.command.GetAllUpgradeInfoCommand;
import com.supergroup.kos.building.domain.command.GetQueenBuildingInfo;
import com.supergroup.kos.building.domain.command.GetUpgradeInfoCommand;
import com.supergroup.kos.building.domain.command.GetUpgradeStatusCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.UpdateMpCommand;
import com.supergroup.kos.building.domain.command.UpgradeBuildingCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.QueenBuildingConfig;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.QueenBuildingService;
import com.supergroup.kos.building.domain.service.point.PointService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.queen.QueenConfigService;
import com.supergroup.kos.building.domain.service.upgrade.UpgradeService;
import com.supergroup.kos.dto.building.QueenBuildingResponse;
import com.supergroup.kos.dto.building.SpinQueenRequest;
import com.supergroup.kos.dto.upgrade.UpgradeStatusResponse;
import com.supergroup.kos.mapper.QueenBuildingMapper;
import com.supergroup.kos.mapper.QueenMapper;
import com.supergroup.kos.mapper.UpgradeQueenBuildingInfoMapper;
import com.supergroup.kos.mapper.UpgradeSessionMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/building/queen")
@RequiredArgsConstructor
public class QueenBuildingRestController {
    private final QueenBuildingService           queenBuildingService;
    private final QueenConfigService             queenConfigService;
    private final KosProfileService              kosProfileService;
    private final AssetsService                  assetsService;
    private final AssetService                   assetService;
    private final PointService                   pointService;
    private final UpgradeService                 upgradeService;
    private final QueenMapper                    queenMapper;
    private final QueenBuildingMapper            queenBuildingMapper;
    private final UpgradeQueenBuildingInfoMapper upgradeQueenBuildingInfoMapper;
    private final BuildingConfigDataSource       buildingConfigDataSource;
    private final UpgradeSessionMapper           upgradeSessionMapper;

    @GetMapping("")
    public ResponseEntity<QueenBuildingResponse> getBuildingInfo() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var queenBuilding = queenBuildingService.getBuildingInfo(new GetQueenBuildingInfo(kosProfile.getId()));
        var queenBuildingConfig = (QueenBuildingConfig) buildingConfigDataSource.getConfig(BuildingName.QUEEN, queenBuilding.getLevel());
        var maxQueen = queenBuildingConfig.getMaxQueen();
        return ResponseEntity.ok(queenBuildingMapper.toDTO(queenBuilding).setMaxQueen(maxQueen));
    }

    @PostMapping("/spin")
    public ResponseEntity<?> spinQueen(@Valid @RequestBody SpinQueenRequest spinQueenRequest) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var numberOfSpin = spinQueenRequest.getNumberOfQueenCard();
        AtomicReference<Long> extraMp = new AtomicReference<>(0L);
        var queenSpins = queenBuildingService.spinQueen(numberOfSpin, kosProfile.getId());
        var queenConfigIdSpined = new ArrayList<Long>();
        var queenResponses = queenMapper.toDTOs(queenSpins).stream().map(queenResponse -> {
            var queenConfigId = queenResponse.getModel().getId();
            Boolean isNew = !queenConfigService.isExist(kosProfile.getId(), queenConfigId)
                            && queenConfigIdSpined.stream().noneMatch(id -> id.equals(queenConfigId));
            queenConfigIdSpined.add(queenConfigId);
            var thumbnail = assetService.getUrl(queenResponse.getModel().getThumbnail());
            if (isNew.equals(true)) {
                extraMp.updateAndGet(v -> v + queenResponse.getModel().getMp());
            }
            return queenResponse.setModel(queenResponse.getModel().setIsNew(isNew).setThumbnail(thumbnail));
        }).collect(Collectors.toList());
        pointService.updateMp(new UpdateMpCommand().setDiffMp(extraMp.get()).setKosProfileId(kosProfile.getId()));
        var response = numberOfSpin == 1 ? queenResponses.get(0) : queenResponses;
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upgrade")
    public ResponseEntity<?> upgrade() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var queenBuilding = queenBuildingService.getBuildingInfo(new GetQueenBuildingInfo(kosProfile.getId()));
        var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
        upgradeService.upgrade(new UpgradeBuildingCommand(kosProfile, queenBuilding, asset));
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/upgrade")
    public ResponseEntity<?> getUpgradeInfo(@RequestParam(value = "level", required = false) Long level) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        if (Objects.isNull(level)) {
            var queenBuildingConfigs = (List<QueenBuildingConfig>) queenBuildingService.getAllUpgradeInfo(
                    new GetAllUpgradeInfoCommand(BuildingName.QUEEN, kosProfile.getId()));
            return ResponseEntity.ok(Map.of("details", upgradeQueenBuildingInfoMapper.toDTOs(queenBuildingConfigs)));
        } else {
            var queenBuildingConfig = (QueenBuildingConfig) queenBuildingService.getUpgradeInfo(new GetUpgradeInfoCommand(level, kosProfile.getId()));
            var response = upgradeQueenBuildingInfoMapper.toDTO(queenBuildingConfig);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/upgrade/status")
    public ResponseEntity<UpgradeStatusResponse> upgradeStatus() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        UpgradeSession upgradeSession = upgradeService.getUpgradeSession(new GetUpgradeStatusCommand().setKosProfileId(kosProfile.getId())
                                                                                                      .setBuildingName(BuildingName.QUEEN));
        return ResponseEntity.ok(upgradeSessionMapper.toUpgradeStatusResponse(upgradeSession));
    }

}
