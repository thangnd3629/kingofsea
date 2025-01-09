package com.supergroup.kos.api.mining;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetDataMiningCommand;
import com.supergroup.kos.building.domain.constant.MiningType;
import com.supergroup.kos.building.domain.model.mining.DataMining;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.CastleBuildingService;
import com.supergroup.kos.building.domain.service.building.StoneMineService;
import com.supergroup.kos.building.domain.service.building.WoodMineService;
import com.supergroup.kos.building.domain.service.mining.DataMiningService;
import com.supergroup.kos.building.domain.service.mining.PeopleAndGoldMiningService;
import com.supergroup.kos.building.domain.service.point.PointService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.dto.mining.ClaimMineResponse;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/mining")
@RequiredArgsConstructor
public class MiningRestController {

    private final KosProfileService kosProfileService;

    private final StoneMineService           stoneMineService;
    private final WoodMineService            woodMineService;
    private final PeopleAndGoldMiningService peopleAndGoldMiningService;
    private final CastleBuildingService      castleBuildingService;
    private final BuildingConfigDataSource   buildingConfigDataSource;
    private final PointService               pointService;
    private final AssetsService              assetsService;
    private final DataMiningService          dataMiningService;

    @GetMapping("/data")
    public ResponseEntity<DataMining> getDataMining(@RequestParam(name = "type") MiningType type) {
        GetDataMiningCommand command = new GetDataMiningCommand().setUseId(AuthUtil.getUserId()).setMiningType(type);
        return ResponseEntity.ok(dataMiningService.getDataMiningService(command));
    }

    @GetMapping("/stone")
    public ResponseEntity<ClaimMineResponse> claimStone() {
        var kosProfile = kosProfileService.findByUserId(AuthUtil.getUserId())
                                          .orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND));
        var res = stoneMineService.claimStone(kosProfile.getId());
        return ResponseEntity.ok(new ClaimMineResponse(res.getIncrease().longValue(), res.getTotal().longValue(), res.getLastTimeClaim()));
    }

    @GetMapping("/wood")
    public ResponseEntity<ClaimMineResponse> claimWood() {
        var kosProfile = kosProfileService.findByUserId(AuthUtil.getUserId())
                                          .orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND));
        var res = woodMineService.claimWood(kosProfile.getId());
        return ResponseEntity.ok(new ClaimMineResponse(res.getIncrease().longValue(), res.getTotal().longValue(), res.getLastTimeClaim()));
    }

    @GetMapping("/people")
    public ResponseEntity<ClaimMineResponse> claimPeople() {
        var kosProfile = kosProfileService.findByUserId(AuthUtil.getUserId())
                                          .orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND));
//        var castleBuilding = castleBuildingService.getCastleBuilding(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
//        var castleConfig = (CastleConfig) buildingConfigDataSource.getConfig(BuildingName.CASTLE, castleBuilding.getLevel())
//                                                                  .orElseThrow(() -> KOSException.of(ErrorCode.CONFIG_NOT_FOUND));
//        var point = pointService.getKosPoint(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
//        var snapshot = new PeopleAndGoldMiningSnapshot().setIdlePeople(castleBuilding.getIdlePeople())
//                                                        .setMaxPopulation(castleConfig.getMaxPopulation())
//                                                        .setMp(point.getMpPoint())
//                                                        .setPopulationGrowthBase(castleConfig.getPopulationGrowthBase())
//                                                        .setMpMultiplier(castleConfig.getMpMultiplier())
//                                                        .setGoldPerPerson(castleConfig.getGoldPerPerson());
        var res = castleBuildingService.claimPeopleAndGold(kosProfile.getId());
        return ResponseEntity.ok(
                new ClaimMineResponse(res.getIncreasePeople().longValue(), res.getTotalPeople().longValue(), res.getLastTimeClaim()));
    }

    @GetMapping("/gold")
    public ResponseEntity<ClaimMineResponse> claimGold() {
        var kosProfile = kosProfileService.findByUserId(AuthUtil.getUserId())
                                          .orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND));
//        var castleBuilding = castleBuildingService.getCastleBuilding(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
//        var castleConfig = (CastleConfig) buildingConfigDataSource.getConfig(BuildingName.CASTLE, castleBuilding.getLevel())
//                                                                  .orElseThrow(() -> KOSException.of(ErrorCode.CONFIG_NOT_FOUND));
//        var point = pointService.getKosPoint(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
//        var snapshot = new PeopleAndGoldMiningSnapshot().setIdlePeople(castleBuilding.getIdlePeople())
//                                                        .setMaxPopulation(castleConfig.getMaxPopulation())
//                                                        .setMp(point.getMpPoint())
//                                                        .setPopulationGrowthBase(castleConfig.getPopulationGrowthBase())
//                                                        .setMpMultiplier(castleConfig.getMpMultiplier())
//                                                        .setGoldPerPerson(castleConfig.getGoldPerPerson());
        var res = castleBuildingService.claimPeopleAndGold(kosProfile.getId());
//        var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(kosProfile.getId()));
//        asset.setGold(asset.getGold() + res.getIncreaseGold());
//        castleBuilding.setIdlePeople(castleBuilding.getIdlePeople() + res.getIncreasePeople()).setLastTimeClaim(res.getLastTimeClaim());
//        assetsService.save(asset);
//        castleBuildingService.save(castleBuilding);
        return ResponseEntity.ok(new ClaimMineResponse(res.getIncreaseGold().longValue(),
                                                       res.getTotalGold().longValue(),
                                                       res.getLastTimeClaim()));
    }

}

