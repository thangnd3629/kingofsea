package com.supergroup.kos.api.building;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetBuildingInfoCommand;
import com.supergroup.kos.building.domain.command.GetMpFromQueenCommand;
import com.supergroup.kos.building.domain.command.GetMpFromRelicCommand;
import com.supergroup.kos.building.domain.command.GetUpgradeStatusCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.UpgradeBuildingCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.StorageType;
import com.supergroup.kos.building.domain.model.building.StorageBuilding;
import com.supergroup.kos.building.domain.model.config.CastleConfig;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.CastleBuildingService;
import com.supergroup.kos.building.domain.service.building.StoneMineService;
import com.supergroup.kos.building.domain.service.building.StorageBuildingService;
import com.supergroup.kos.building.domain.service.building.WoodMineService;
import com.supergroup.kos.building.domain.service.config.KosConfigService;
import com.supergroup.kos.building.domain.service.point.PointService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.queen.QueenService;
import com.supergroup.kos.building.domain.service.relic.RelicService;
import com.supergroup.kos.building.domain.service.upgrade.UpgradeService;
import com.supergroup.kos.dto.building.CastleConfigReward;
import com.supergroup.kos.dto.building.CastleDetailResponse;
import com.supergroup.kos.dto.building.CastleOverviewResponse;
import com.supergroup.kos.dto.building.CastleUpgradeInfoResponse;
import com.supergroup.kos.dto.building.InputGoldResponse;
import com.supergroup.kos.dto.building.PopulationCompositionResponse;
import com.supergroup.kos.dto.building.SocialAffectionResponse;
import com.supergroup.kos.mapper.CastleConfigRewardMapper;
import com.supergroup.kos.mapper.CastleUpgradeInfoMapper;
import com.supergroup.kos.mapper.UpgradeSessionMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/user/castle")
@RequiredArgsConstructor
public class CastleBuildingRestController {
    private final KosProfileService        kosProfileService;
    private final CastleBuildingService    castleBuildingService;
    private final QueenService             queenService;
    private final RelicService             relicService;
    private final PointService             pointService;
    private final CastleConfigRewardMapper castleConfigRewardMapper;
    private final CastleUpgradeInfoMapper  castleUpgradeInfoMapper;
    private final UpgradeService           upgradeService;
    private final BuildingConfigDataSource buildingConfigDataSource;
    private final StoneMineService         stoneMineService;
    private final WoodMineService          woodMineService;
    private final AssetsService            assetService;
    private final UpgradeSessionMapper     upgradeSessionMapper;
    private final StorageBuildingService   storageBuildingService;
    private final KosConfigService         kosConfigService;

    @GetMapping("/overview")
    public ResponseEntity<CastleOverviewResponse> getOverview() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var kosProfileId = kosProfile.getId();
        var castleBuildingDetail = castleBuildingService.getCastleBuildingDetail(new KosProfileCommand().setKosProfileId(kosProfileId));
        var kosPoint = pointService.getKosPoint(kosProfile);
        var assets = assetService.getAssets(new KosProfileCommand().setKosProfileId(kosProfileId));
        var stoneMine = stoneMineService.getBuildingInfo(new GetBuildingInfoCommand(kosProfileId).setCheckValidUnlock(false));
        var woodMine = woodMineService.getBuildingInfo(new GetBuildingInfoCommand(kosProfileId).setCheckValidUnlock(false));
        var response = new CastleOverviewResponse();

        var mpFromQueens = queenService.getMpFromQueens(new GetMpFromQueenCommand().setKosProfile(kosProfile));
        var mpFromRelics = relicService.getMpFromRelicListings(new GetMpFromRelicCommand().setKosProfileId(kosProfileId));
        var mpCastle = 0L;
        // decrease mp when base occupied
        if (kosProfile.getBase().isOccupied()) {
            mpCastle = Math.round(kosProfileService.getInitAssetConfig().getMp() * (1 - kosConfigService.occupyEffect().getDecreaseMp()));
        } else {
            mpCastle = kosProfileService.getInitAssetConfig().getMp();
        }

        response.setLevel(castleBuildingDetail.getLevel())
                .setWinStreak(10L) // TODO fix hard code
                .setIslandStatus(castleBuildingDetail.getIslandStatus())
                .setMpPoint(mpFromQueens + mpFromRelics + mpCastle)
                .setGpPoint(kosPoint.getGpPoint())
                .setTpPoint(kosPoint.getTpPoint())
                .setPeopleProduction(castleBuildingDetail.getPeopleProduction())
                .setWoodProduction(woodMine.getProduction())
                .setStoneProduction(stoneMine.getProduction())
                .setGoldProduction(castleBuildingDetail.getGoldProduction())
                .setTotalPeople(assets.getTotalPeople().longValue())
                .setIdlePeople(castleBuildingDetail.getIdlePeople().longValue())
                .setMaxPeople(castleBuildingDetail.getMaxPopulation())
                .setMpMultiplier(castleBuildingDetail.getMpMultiplier())
                .setGold(assets.getGold().longValue());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/population-composition")
    public ResponseEntity<PopulationCompositionResponse> getPopulation() {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        var castleBuilding = castleBuildingService.getCastleBuildingDetail(new KosProfileCommand().setKosProfileId(kosProfileId));
        var assets = assetService.getAssets(new KosProfileCommand().setKosProfileId(kosProfileId));
        var response = new PopulationCompositionResponse();
        var stoneMine = stoneMineService.getBuildingInfo(new GetBuildingInfoCommand(kosProfileId).setCheckValidUnlock(false));
        var woodMine = woodMineService.getBuildingInfo(new GetBuildingInfoCommand(kosProfileId).setCheckValidUnlock(false));
        response.setTotalPeople(assets.getTotalPeople().longValue())
                .setIdle(castleBuilding.getIdlePeople().longValue())
                .setMason(stoneMine.getWorker())
                .setCarpenter(woodMine.getWorker())  // TODO this is hard code
                .setMaxPeople(castleBuilding.getMaxPopulation());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/social")
    public ResponseEntity<SocialAffectionResponse> getSocial() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var kosProfileId = kosProfile.getId();
        var mpFromQueens = queenService.getMpFromQueens(new GetMpFromQueenCommand().setKosProfile(kosProfile));
        var mpFromRelics = relicService.getMpFromRelicListings(new GetMpFromRelicCommand().setKosProfileId(kosProfileId));
        var response = new SocialAffectionResponse();
        response.setMpFromRelic(mpFromRelics)
                .setMpFromQueens(mpFromQueens)
                .setPeopleProduction(
                        castleBuildingService.getCastleBuildingDetail(new KosProfileCommand().setKosProfileId(kosProfileId)).getPeopleProduction());
        // decrease mp when base occupied
        if (kosProfile.getBase().isOccupied()) {
            response.setMpFromCastle(
                    Math.round(kosProfileService.getInitAssetConfig().getMp() * (1 - kosConfigService.occupyEffect().getDecreaseMp())));
        } else {
            response.setMpFromCastle(kosProfileService.getInitAssetConfig().getMp());
        }
        response.setTotalMp(response.getMpFromCastle() + response.getMpFromQueens() + response.getMpFromRelic());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/input-gold")
    public ResponseEntity<InputGoldResponse> getInputGold() {
        var response = new InputGoldResponse();
        // TODO this is fake data?
        response.setIdlePerHour(10D); //ipm late
        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail")
    public ResponseEntity<CastleDetailResponse> getDetail() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var castleBuilding = castleBuildingService.getCastleBuilding(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
        var response = new CastleDetailResponse();
        var configs = (List<CastleConfig>) buildingConfigDataSource.getListConfig(BuildingName.CASTLE);

        // TODO check if refactor mining
        StorageBuilding storageBuilding = storageBuildingService.findByKosProfileIdAndStorageType(kosProfile.getId(), StorageType.GOLD)
                                                                .orElseThrow(() -> KOSException.of(ErrorCode.STORAGE_BUILDING_NOT_FOUND));
        Boolean isLockStorageGold = storageBuilding.getIsLock();
        List<CastleConfigReward> list = castleConfigRewardMapper.toDTOs(configs).stream().map(is -> {
            if (isLockStorageGold) {
                is.setGoldPerPerson(0D);
            } else {
                is.setGoldPerPerson(is.getGoldPerPerson() * (1 + kosProfile.getBonusGoldProductionPercent()));
            }

            return is;
        }).collect(Collectors.toList());
        response.setCurrentLevel(castleBuilding.getLevel())
                .setData(list);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/upgrade")
    public ResponseEntity<CastleUpgradeInfoResponse> getUpgradeInfo() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var kosProfileId = kosProfile.getId();
        var castleBuilding = castleBuildingService.getCastleBuilding(new KosProfileCommand().setKosProfileId(kosProfileId));
        var config = (CastleConfig) buildingConfigDataSource.getConfig(BuildingName.CASTLE, castleBuilding.getLevel() + 1); // next level
        var response = castleUpgradeInfoMapper.toDTO(config);
        response.setCurrentLevel(castleBuilding.getLevel())
                .setNextLevel(castleBuilding.getLevel() + 1)
                .setCurrentPopulation(castleBuilding.getIdlePeople().longValue())
                .setCurrentMaxPopulation(config.getMaxPopulation())
                .setCostGold(config.getGold())
                .setCostStone(config.getStone())
                .setCostWood(config.getWood())
                .setGpGain(config.getGpPointReward())
                .setUpgradeDuration(Double.valueOf(config.getUpgradeDuration() * (1 - kosProfile.getReduceUpgradingTimePercent())).longValue());
        return ResponseEntity.ok(response);
    }

    @PostMapping("upgrade")
    public ResponseEntity<?> upgrade() {
        var kosProfile = kosProfileService.findByUserId(AuthUtil.getUserId())
                                          .orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND));
        var castleBuilding = castleBuildingService.findByKosProfile_Id(kosProfile.getId())
                                                  .orElseThrow(() -> KOSException.of(ErrorCode.CASTLE_BUILDING_NOT_FOUND));
        var asset = assetService.getAssets(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
        upgradeService.upgrade(new UpgradeBuildingCommand(kosProfile, castleBuilding, asset));
        return ResponseEntity.ok().build();
    }

    @GetMapping("upgrade/status")
    public ResponseEntity<?> getUpgradeStatus() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        UpgradeSession upgradeSession = upgradeService.getUpgradeSession(new GetUpgradeStatusCommand().setKosProfileId(kosProfile.getId())
                                                                                                      .setBuildingName(BuildingName.CASTLE));
        return ResponseEntity.ok(upgradeSessionMapper.toUpgradeStatusResponse(upgradeSession));
    }

}
