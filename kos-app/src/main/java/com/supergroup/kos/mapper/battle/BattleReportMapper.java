package com.supergroup.kos.mapper.battle;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.supergroup.asset.service.AssetService;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.constant.BattleProfileType;
import com.supergroup.kos.building.domain.constant.battle.BattleResult;
import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.constant.battle.BattleType;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.dto.seamap.CoordinatesDTO;
import com.supergroup.kos.building.domain.dto.seamap.MoveSessionDTO;
import com.supergroup.kos.building.domain.model.battle.BattleFinalReport;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;
import com.supergroup.kos.building.domain.model.battle.BattleReport;
import com.supergroup.kos.building.domain.model.battle.BattleRound;
import com.supergroup.kos.building.domain.model.battle.BossReport;
import com.supergroup.kos.building.domain.model.battle.DamageReport;
import com.supergroup.kos.building.domain.model.battle.EscortShipReport;
import com.supergroup.kos.building.domain.model.battle.IBattleReport;
import com.supergroup.kos.building.domain.model.battle.MotherShipReport;
import com.supergroup.kos.building.domain.model.battle.ReverseEscortShipReport;
import com.supergroup.kos.building.domain.model.battle.ReverseMotherShipReport;
import com.supergroup.kos.building.domain.model.battle.RoundReport;
import com.supergroup.kos.building.domain.model.config.seamap.BossSeaConfig;
import com.supergroup.kos.building.domain.model.item.Item;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.ResourceIsland;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRoundRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementConfigRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementRepository;
import com.supergroup.kos.building.domain.service.config.KosConfigService;
import com.supergroup.kos.dto.battle.BattleProfileResponse;
import com.supergroup.kos.dto.battle.BattleReportDetailResponse;
import com.supergroup.kos.dto.battle.BattleReportResponse;
import com.supergroup.kos.dto.battle.BossReportResponse;
import com.supergroup.kos.dto.battle.DamageRoundReportResponse;
import com.supergroup.kos.dto.battle.EscortShipReportResponse;
import com.supergroup.kos.dto.battle.FinalReportResponse;
import com.supergroup.kos.dto.battle.MotherShipReportResponse;
import com.supergroup.kos.dto.battle.ResourceResponse;
import com.supergroup.kos.dto.battle.RoundReportDetailsResponse;
import com.supergroup.kos.dto.battle.RoundReportResponse;
import com.supergroup.kos.dto.battle.ShipReportResponse;
import com.supergroup.kos.dto.battle.UsedItemResponse;

@Mapper
public interface BattleReportMapper {

    CoordinatesDTO map(Coordinates coordinates);

    default BattleProfileResponse map(BattleProfile battleProfile, AssetService assetService, SeaElementConfigRepository seaElementConfigRepository) {
        if (battleProfile.getType().equals(BattleProfileType.USER)) {
            return new BattleProfileResponse().setKosProfileId(battleProfile.getKosProfile().getId())
                                              .setCoordinates(map(battleProfile.getCoordinates()))
                                              .setType(battleProfile.getType())
                                              .setUsername(battleProfile.getKosProfile()
                                                                        .getUser()
                                                                        .getUserProfile()
                                                                        .getUsername())
                                              .setAvatarUrl(assetService.getUrl(battleProfile.getKosProfile()
                                                                                             .getUser()
                                                                                             .getUserProfile()
                                                                                             .getAvatarUrl()));
        } else if (battleProfile.getType().equals(BattleProfileType.BOSS)) {
            BattleProfileResponse battleProfileResponse = new BattleProfileResponse().setBossId(battleProfile.getBossSea().getId())
                                                                                     .setType(battleProfile.getType())
                                                                                     .setCoordinates(map(battleProfile.getCoordinates()))
                                                                                     .setBossConfigId(battleProfile.getBossSea().getConfigId());
            Optional optional = seaElementConfigRepository.findBossSeaConfigById(battleProfile.getBossSea().getConfigId());
            if (optional.isPresent() && optional.get() instanceof BossSeaConfig) {
                BossSeaConfig config = (BossSeaConfig) optional.get();
                battleProfileResponse.setUsername(config.getName())
                                     .setAvatarUrl(assetService.getUrl(config.getThumbnail()));
            }
            return battleProfileResponse;
        }
        return null;
    }

    default BattleReportResponse map(BattleReport report,
                                     KosProfile myKosProfile,
                                     AssetService assetService,
                                     SeaElementConfigRepository seaElementConfigRepository) {
        var response = new BattleReportResponse();
        response.setId(report.getBattle().getId());
        response.setType(report.getBattle().getBattleType().getDisplayName());
        if (Objects.nonNull(report.getResourceType())) {
            response.setResourceType(report.getResourceType().name());
        }
        response.setAttacker(map(report.getInitiator(), assetService, seaElementConfigRepository));
        response.setDefender(map(report.getVictim(), assetService, seaElementConfigRepository));
        if (Objects.nonNull(report.getWinner())) {
            response.setWinner(report.getWinner().getId());
        }
        if (Objects.nonNull(report.getLoser())) {
            response.setLoser(report.getLoser().getId());
        }
        if (report.getBattle().getStatus().equals(BattleStatus.END)) {
            if (Objects.isNull(report.getWinner())) {
                response.setResult(BattleResult.UNDEFINED);
            } else if (report.getBattle().getDefender().getType().equals(BattleProfileType.USER)) {
                if (myKosProfile.getId().equals(report.getWinner().getKosProfile().getId())) {
                    response.setResult(BattleResult.WIN);
                } else if (myKosProfile.getId().equals(report.getLoser().getKosProfile().getId())) {
                    response.setResult(BattleResult.LOSE);
                } else {
                    response.setResult(BattleResult.UNDEFINED);
                }
            } else {
                if (report.getWinner().getType().equals(BattleProfileType.BOSS)) {
                    response.setResult(BattleResult.LOSE);
                } else if (myKosProfile.getId().equals(report.getWinner().getKosProfile().getId())) {
                    response.setResult(BattleResult.WIN);
                } else {
                    response.setResult(BattleResult.UNDEFINED);
                }
            }
        }
        response.setCoordinates(map(report.getCoordinates()));
        response.setStatus(report.getBattle().getStatus());
        response.setStartAt(report.getStartAt());
        response.setEndAt(report.getEndAt());
        response.setUpdatedAt(report.getUpdatedAt());
        response.setAmountRound((long) report.getRoundReports().size());
        response.setRoundIds(report.getRoundReports()
                                   .stream()
//                                   .sorted(Comparator.comparingInt(o -> o.getRound().getIndex().intValue()))
                                   .map(roundReport -> roundReport.getRound().getId())
                                   .collect(Collectors.toList()));
        return response;
    }

    default BattleReportResponse map(IBattleReport report,
                                     KosProfile myKosProfile,
                                     AssetService assetService,
                                     BattleRoundRepository battleRoundRepository,
                                     SeaElementConfigRepository seaElementConfigRepository) {
        var response = new BattleReportResponse();
        response.setId(report.getBattleId());
        response.setType(report.getBattleType().getDisplayName());
        if (Objects.nonNull(report.getResourceType())) {
            response.setResourceType(report.getResourceType().name());
        }
        response.setAttacker(map(report.initiatorProfile(), assetService, seaElementConfigRepository));
        response.setDefender(map(report.victimProfile(), assetService, seaElementConfigRepository));
        response.setWinner(report.getWinnerId());
        response.setLoser(report.getLoserId());
        response.setResult(report.result(myKosProfile.getId()));
        response.setCoordinates(map(report.coordinate()));
        response.setStatus(report.getStatus());
        response.setStartAt(report.getStartAt());
        response.setEndAt(report.getEndAt());
        response.setUpdatedAt(report.getUpdatedAt());
        response.setAmountRound(report.getAmountRound());
        response.setRoundIds(battleRoundRepository.roundIdsOfBattle(report.getBattleId()));
        return response;
    }

    FinalReportResponse map(BattleFinalReport battleFinalReport);

    default BattleReportDetailResponse map(BattleReport battleReport, @Context KosConfigService kosConfigService) {
        ResourceResponse victoryReward = null;
        if (Objects.nonNull(battleReport.getReward())) {
            victoryReward = new ResourceResponse().setGold(battleReport.getReward().getGold().longValue())
                                                  .setWood(battleReport.getReward().getWood().longValue())
                                                  .setStone(battleReport.getReward().getStone().longValue())
                                                  .setRelics(battleReport.getReward()
                                                                         .getRelics()
                                                                         .stream()
                                                                         .map(relic -> relic.getRelicConfig().getId())
                                                                         .collect(Collectors.toList()))
                                                  .setQueens(battleReport.getReward()
                                                                         .getQueens()
                                                                         .stream()
                                                                         .map(queen -> queen.getQueenConfig().getId())
                                                                         .collect(Collectors.toList()))
                                                  .setGloryPoint(battleReport.getReward().getGloryPoint())
                                                  .setItems(battleReport.getReward()
                                                                        .getItems()
                                                                        .stream()
                                                                        .map(Item::getId)
                                                                        .collect(Collectors.toList()))
                                                  .setWeapons(battleReport.getReward()
                                                                          .getWeapons()
                                                                          .stream()
                                                                          .map(w -> w.getWeaponConfig().getId())
                                                                          .collect(Collectors.toList()));
        }
        ResourceResponse defeatLoss = null;
        // set defeat effect when defender lose
        if (battleReport.getBattle().getBattleType().equals(BattleType.OCCUPY)
            && Objects.nonNull(battleReport.getWinner())
            && battleReport.getWinner().getId().equals(battleReport.getInitiator().getId())) {
            defeatLoss = new ResourceResponse().setEffect(kosConfigService.occupyEffect());
        } else {
            defeatLoss = victoryReward;
        }
        var attackerFinalReport = map(battleReport.getAttackerFinalReport());
        var defenderFinalReport = map(battleReport.getDefenderFinalReport());
        return new BattleReportDetailResponse()
                .setAttacker(attackerFinalReport)
                .setDefender(defenderFinalReport)
                .setVictoryReward(victoryReward)
                .setDefeatLoss(defeatLoss)
                .setRoundIds(battleReport.getBattle()
                                         .getBattleRounds()
                                         .stream()
                                         .map(BattleRound::getId)
                                         .sorted(Comparator.comparingLong(v -> v))
                                         .collect(Collectors.toList()));
    }

    default RoundReportResponse map(RoundReport roundReport, AssetService assetService) {
        var res = new RoundReportResponse();
        var listAttackerMotherShip = mapMotherShip(roundReport.getAttackerMotherShipReports());
        var listAttackerEscortShip = mapEscortShip(roundReport.getAttackerEscortShipReports());
        var listDefenderMotherShip = mapMotherShip(roundReport.getDefenderMotherShipReports());
        var listDefenderEscortShip = mapEscortShip(roundReport.getDefenderEscortShipReports());
        var listAttackerUsedItem = roundReport.getAttackerUsedItem()
                                              .stream()
                                              .map(roundUsedItem -> {
                                                  return new UsedItemResponse().setItems(roundUsedItem.getItems()
                                                                                                      .stream()
                                                                                                      .map(Item::getId)
                                                                                                      .collect(Collectors.toList()))
                                                                               .setUsername(roundUsedItem.getBattleProfile()
                                                                                                         .getUsername())
                                                                               .setAvatarUrl(assetService.getUrl(roundUsedItem.getBattleProfile()
                                                                                                                              .getAvatar()))
                                                                               .setCoordinates(map(roundUsedItem.getBattleProfile()
                                                                                                                .getCoordinates()));
                                              }).collect(Collectors.toList());
        var listDefenderUsedItem = roundReport.getDefenderUsedItem()
                                              .stream()
                                              .map(roundUsedItem -> new UsedItemResponse().setItems(roundUsedItem.getItems()
                                                                                                                 .stream()
                                                                                                                 .map(Item::getId)
                                                                                                                 .collect(Collectors.toList()))
                                                                                          .setUsername(roundUsedItem.getBattleProfile()
                                                                                                                    .getUsername())
                                                                                          .setAvatarUrl(
                                                                                                  assetService.getUrl(roundUsedItem.getBattleProfile()
                                                                                                                                   .getAvatar()))
                                                                                          .setCoordinates(map(roundUsedItem.getBattleProfile()
                                                                                                                           .getCoordinates())))
                                              .collect(Collectors.toList());

        var listAttackerReserveMotherShips = mapReverseMotherShip(roundReport.getAttackerReserveMotherShipReports());
        var listAttackerReserveEscortShips = mapReverseEscortShip(roundReport.getAttackerReserveEscortShipReports());
        var listDefenderReserveMotherShips = mapReverseMotherShip(roundReport.getDefenderReserveMotherShipReports());
        var listDefenderReserveEscortShips = mapReverseEscortShip(roundReport.getDefenderReserveEscortShipReports());

        res.setAttacker(new ShipReportResponse().setMotherShips(listAttackerMotherShip)
                                                .setEscortShip(listAttackerEscortShip)
                                                .setReserveEscortShips(listAttackerReserveEscortShips)
                                                .setReserveMotherShips(listAttackerReserveMotherShips)
                                                .setUsedItem(listAttackerUsedItem));
        res.setDefender(new ShipReportResponse().setMotherShips(listDefenderMotherShip)
                                                .setEscortShip(listDefenderEscortShip)
                                                .setReserveEscortShips(listDefenderReserveEscortShips)
                                                .setReserveMotherShips(listDefenderReserveMotherShips)
                                                .setUsedItem(listDefenderUsedItem));

        if (Objects.nonNull(roundReport.getDefenderBossReports()) && !roundReport.getDefenderBossReports().isEmpty()) {
            res.getDefender().setBoss(map(roundReport.getDefenderBossReports().get(0)));
        }

        return res;
    }

    default RoundReportDetailsResponse toRoundReportDetail(RoundReport roundReport) {
        var res = new RoundReportDetailsResponse();
        res.setDefender(map(roundReport.getDefenderDamageReport()));
        res.setAttacker(map(roundReport.getAttackerDamageReport()));
        return res;
    }

    List<BossReportResponse> map(List<BossReport> bossReport);

    BossReportResponse map(BossReport bossReport);

    @Mappings({
            @Mapping(source = "escortShip", target = "amountEscortShip"),
            @Mapping(source = "motherShip", target = "amountMotherShip")
    })
    DamageRoundReportResponse map(DamageReport damageReport);

    @Mappings({
            @Mapping(source = "escortShipType", target = "shipType"),
            @Mapping(source = "escortShipGroupName", target = "groupName"),
            @Mapping(source = "escortShipType.indexInCombat", target = "index")
    })
    EscortShipReportResponse map(EscortShipReport escortShipReport);

    @Mappings({
            @Mapping(source = "escortShipType", target = "shipType"),
            @Mapping(source = "escortShipGroupName", target = "groupName"),
            @Mapping(source = "escortShipType.indexInCombat", target = "index")
    })
    EscortShipReportResponse map(ReverseEscortShipReport escortShipReport);

    List<EscortShipReportResponse> mapEscortShip(List<EscortShipReport> escortShipReport);

    List<EscortShipReportResponse> mapReverseEscortShip(List<ReverseEscortShipReport> escortShipReport);

    @Mappings({
            @Mapping(source = "battleProfile.username", target = "owner"),
    })
    MotherShipReportResponse map(MotherShipReport motherShipReport);

    @Mappings({
            @Mapping(source = "battleProfile.username", target = "owner"),
    })
    MotherShipReportResponse map(ReverseMotherShipReport motherShipReport);

    List<MotherShipReportResponse> mapMotherShip(List<MotherShipReport> motherShipReports);

    List<MotherShipReportResponse> mapReverseMotherShip(List<ReverseMotherShipReport> motherShipReports);

    default MoveSessionDTO map(MoveSession moveSession, SeaElementRepository<ResourceIsland> resourceIslandRepo) {
        var dto = new MoveSessionDTO().setEnd(moveSession.getEnd())
                                      .setStart(moveSession.getStart())
                                      .setSpeed(moveSession.getSpeed())
                                      .setDestinationType(moveSession.getDestinationType())
                                      .setDestinationElementId(moveSession.getDestinationElementId())
                                      .setTimeStart(moveSession.getTimeStart());
        if (moveSession.getDestinationType().equals(SeaElementType.RESOURCE)) {
            var island = resourceIslandRepo.findById(moveSession.getDestinationElementId()).orElseThrow(
                    () -> KOSException.of(ErrorCode.ELEMENT_NOT_FOUND));
            dto.setResourceType(island.getResourceType());
        }
        return dto;
    }
}
