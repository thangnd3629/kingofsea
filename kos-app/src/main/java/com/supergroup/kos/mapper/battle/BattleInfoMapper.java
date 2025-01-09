package com.supergroup.kos.mapper.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.supergroup.asset.service.AssetService;
import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.constant.battle.FactionType;
import com.supergroup.kos.building.domain.constant.battle.ShipType;
import com.supergroup.kos.building.domain.dto.seamap.CoordinatesDTO;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;
import com.supergroup.kos.building.domain.model.battle.BattleRoundSnapshot;
import com.supergroup.kos.building.domain.model.battle.BattleUnit;
import com.supergroup.kos.building.domain.model.battle.MotherShipBattle;
import com.supergroup.kos.building.domain.model.battle.logic.Attack;
import com.supergroup.kos.building.domain.model.config.EscortShipConfig;
import com.supergroup.kos.building.domain.model.config.battle.BattleFieldLineUpConfig;
import com.supergroup.kos.building.domain.model.config.battle.BattleTimeConfig;
import com.supergroup.kos.building.domain.model.seamap.EscortShipSquad;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.model.ship.EscortShip;
import com.supergroup.kos.building.domain.model.ship.MotherShip;
import com.supergroup.kos.building.domain.service.battle.BattlePvPService;
import com.supergroup.kos.building.domain.service.config.KosConfigService;
import com.supergroup.kos.building.domain.service.seamap.activity.LineUpService;
import com.supergroup.kos.building.domain.service.ship.EscortShipConfigService;
import com.supergroup.kos.dto.battle.AttackResponse;
import com.supergroup.kos.dto.battle.BattleInfoResponse;
import com.supergroup.kos.dto.battle.BattleProfileResponse;
import com.supergroup.kos.dto.battle.EscortShipResponse;
import com.supergroup.kos.dto.battle.MotherShipReportResponse;
import com.supergroup.kos.dto.battle.MotherShipResponse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class BattleInfoMapper {
    private final BattlePvPService        battlePvPService;
    private final AssetService            assetService;
    private final KosConfigService        kosConfigService;
    private final EscortShipConfigService escortShipConfigService;
    private final LineUpService           lineUpService;

    public BattleInfoResponse toResponse(Battle battle, BattleRoundSnapshot snapshot) {
        BattleInfoResponse response = new BattleInfoResponse();
        BattleTimeConfig battleTimeConfig = kosConfigService.getBattleTimeConfig();
        response.setId(battle.getId())
                .setCurrentRound(battle.getCurrentRound())
                .setBattleType(battle.getBattleType())
                .setStatus(battle.getStatus())
                .setBattleSite(battle.getBattleSite());
        List<EscortShipConfig> escortShipConfigs = escortShipConfigService.getAll();
        AttackResponse attackerResponse = initAttackResponse(battle.getAttacker(), escortShipConfigs);
        AttackResponse defenderResponse = initAttackResponse(battle.getDefender(), escortShipConfigs);

        if (battle.getStatus().equals(BattleStatus.PROGRESS)) {
            // snapshot
            String keyMotherShip = battlePvPService.builtKeyShip(ShipType.MOTHER_SHIP, null);
            Attack attacker = snapshot.getAttackerModel();
            Attack defender = snapshot.getDefenderModel();

            BattleFieldLineUpConfig battleFieldLineUpConfig = battlePvPService.chooseBattleField(attacker, defender);

            // calculate ship : fighting, reserve
            battlePvPService.settleLineup(attacker, battleFieldLineUpConfig);
            battlePvPService.settleLineup(defender, battleFieldLineUpConfig);

            motherShipBattleToMotherShipResponse(attackerResponse.getMotherShip(), attacker.getBattleFields().get(keyMotherShip));
            motherShipBattleToMotherShipResponse(defenderResponse.getMotherShip(), defender.getBattleFields().get(keyMotherShip));

            escortShipBattleToEscortShipResponse(attackerResponse.getEscortShip(), attacker.getBattleFields());
            escortShipBattleToEscortShipResponse(defenderResponse.getEscortShip(), defender.getBattleFields());

            // waiting
            List<ShipLineUp> shipLineUpsAttacker = lineUpService.getListShipLineupWaitingInBattle(battle.getId(), FactionType.ATTACKER,
                                                                                                  battle.getTimeUpdateStatus());
            List<ShipLineUp> shipLineUpsDefender = lineUpService.getListShipLineupWaitingInBattle(battle.getId(), FactionType.DEFENDER,
                                                                                                  battle.getTimeUpdateStatus());
            update(attackerResponse, shipLineUpsAttacker);
            update(defenderResponse, shipLineUpsDefender);

        }
        if (battle.getStatus().equals(BattleStatus.BREAK)) {
            List<ShipLineUp> shipLineUpsAttacker = lineUpService.getListShipLineupInBattleByFaction(battle.getId(), FactionType.ATTACKER);
            List<ShipLineUp> shipLineUpsDefender = lineUpService.getListShipLineupInBattleByFaction(battle.getId(), FactionType.DEFENDER);

            update(attackerResponse, shipLineUpsAttacker);
            update(defenderResponse, shipLineUpsDefender);

        }

        return response.setAttacker(attackerResponse)
                       .setDefender(defenderResponse);
    }

    private void update(AttackResponse attackResponse, List<ShipLineUp> shipLineUps) {
        for (ShipLineUp shipLineUp : shipLineUps) {
            if (Objects.nonNull(shipLineUp.getMotherShip()) && shipLineUp.getMotherShip().getCurrentHp() > 0) {
                MotherShip motherShip = shipLineUp.getMotherShip();
                MotherShipResponse motherShipResponse = new MotherShipReportResponse();
                motherShipResponse.setModelId(motherShip.getMotherShipConfigQualityConfig().getId())
                                  .setOwner(motherShip.getCommandBuilding().getKosProfile().getUser().getUserProfile().getUsername())
                                  .setQuality(motherShip.getMotherShipConfigQualityConfig().getMotherShipQualityConfig().getQuality())
                                  .setIsWithdrawal(true);
                attackResponse.getMotherShip().add(motherShipResponse);
            }
            List<EscortShipSquad> escortShipSquad = shipLineUp.getEscortShipSquad();
            List<EscortShipResponse> escortShips = attackResponse.getEscortShip();
            for (EscortShipSquad escortShipSquad1 : escortShipSquad) {
                EscortShip escortShip = escortShipSquad1.getEscortShip();
                EscortShipResponse escortShipResponse = escortShips.stream().filter(
                        es -> es.getShipType().equals(escortShip.getEscortShipConfig().getType())).findFirst().orElse(null);
                if (Objects.nonNull(escortShipResponse)) {
                    escortShipResponse.setLeft(escortShipResponse.getLeft() + escortShipSquad1.getAmount() - escortShipSquad1.getKilled());
                }
            }
        }
    }

    private AttackResponse initAttackResponse(BattleProfile battleProfile, List<EscortShipConfig> escortShipConfigs) {
        AttackResponse response = new AttackResponse();
        BattleProfileResponse battleProfileResponse = new BattleProfileResponse();
        if(Objects.nonNull(battleProfile)) {
            battleProfileResponse.setKosProfileId(battleProfile.getKosProfile().getId())
                                 .setType(battleProfile.getType())
                                 .setAvatarUrl(assetService.getUrl(battleProfile.getAvatar()))
                                 .setUsername(battleProfile.getUsername())
                                 .setCoordinates(new CoordinatesDTO().setX(battleProfile.getCoordinates().getX())
                                                                     .setY(battleProfile.getCoordinates().getY()));
        }
        // motherShip
        List<MotherShipResponse> motherShips = new ArrayList<>();
        //escortShip
        List<EscortShipResponse> escortShips = new ArrayList<>();
        for (EscortShipConfig escortShipConfig : escortShipConfigs) {
            EscortShipResponse escortShipResponse = new EscortShipResponse().setShipType(escortShipConfig.getType())
                                                                            .setLeft(0L)
                                                                            .setModelId(escortShipConfig.getId())
                                                                            .setGroupName(escortShipConfig.getEscortShipGroupConfig().getName());
            escortShips.add(escortShipResponse);
        }
        response.setBattleProfile(battleProfileResponse)
                .setMotherShip(motherShips)
                .setEscortShip(escortShips);
        return response;
    }

    private List<MotherShipResponse> motherShipBattleToMotherShipResponse(List<MotherShipResponse> motherShipResponses,
                                                                          List<BattleUnit> shipBattles) {
        if (!shipBattles.isEmpty()) {
            for (BattleUnit shipBattle : shipBattles) {
                if (shipBattle.getFighting() < 1) {
                    MotherShipBattle motherShipBattle = (MotherShipBattle) shipBattle;
                    MotherShipResponse motherShipResponse = new MotherShipReportResponse();
                    motherShipResponse.setModelId(motherShipBattle.getModelId())
                                      .setQuality(motherShipBattle.getQuality())
                                      .setOwner(motherShipBattle.getOwner());
                    motherShipResponses.add(motherShipResponse);
                }
            }
        }
        return motherShipResponses;
    }

    private List<EscortShipResponse> escortShipBattleToEscortShipResponse(List<EscortShipResponse> escortShipResponses,
                                                                          Map<String, List<BattleUnit>> maps) {
        for (EscortShipResponse escortShipResponse : escortShipResponses) {
            String key = battlePvPService.builtKeyShip(ShipType.ESCORT_SHIP, escortShipResponse.getShipType());
            if (maps.containsKey(key)) {
                List<BattleUnit> shipBattles = maps.get(key);
                Long left = 0L;
                for (BattleUnit shipBattle : shipBattles) {
                    left += shipBattle.getReserve();
                }
                escortShipResponse.setLeft(escortShipResponse.getLeft() + left);
            }
        }
        return escortShipResponses;
    }
}
