package com.supergroup.kos.building.domain.service.battle;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.kos.building.domain.constant.battle.ShipStatisticType;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;
import com.supergroup.kos.building.domain.model.battle.BossSeaBattle;
import com.supergroup.kos.building.domain.model.battle.EscortShipBattle;
import com.supergroup.kos.building.domain.model.battle.MotherShipBattle;
import com.supergroup.kos.building.domain.model.config.battle.DefBattleConfig;
import com.supergroup.kos.building.domain.model.config.seamap.BossSeaConfig;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.BossSea;
import com.supergroup.kos.building.domain.model.seamap.EscortShipSquad;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.model.ship.EscortShip;
import com.supergroup.kos.building.domain.model.ship.MotherShip;
import com.supergroup.kos.building.domain.repository.persistence.seamap.LineUpRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementConfigRepository;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.seamap.EscortShipSquadService;
import com.supergroup.kos.building.domain.service.seamap.KosWarInfoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BattleUnitService {
    private final KosWarInfoService          kosWarInfoService;
    private final KosProfileService          kosProfileService;
    private final SeaElementConfigRepository seaElementConfigRepository;
    private final LineUpRepository           lineUpRepository;
    private final EscortShipSquadService     escortShipSquadService;

    @Transactional
    public MotherShipBattle toMotherShipBattle(MotherShip motherShip, DefBattleConfig defBattleConfig) {
        MotherShipBattle model = new MotherShipBattle();
        Double dodge = kosWarInfoService.getMotherShipPower(motherShip, ShipStatisticType.DODGE);
        Double def1 = kosWarInfoService.getMotherShipPower(motherShip, ShipStatisticType.DEF1) * defBattleConfig.getDefPercent()
                      + dodge * defBattleConfig.getDodgePercent();
        Double def2 = kosWarInfoService.getMotherShipPower(motherShip, ShipStatisticType.DEF2) * defBattleConfig.getDefPercent()
                      + dodge * defBattleConfig.getDodgePercent();
        Long maxHp = kosWarInfoService.getMotherShipPower(motherShip, ShipStatisticType.HP).longValue();
        Long currentHp = Objects.nonNull(motherShip.getCurrentHp()) ? motherShip.getCurrentHp() : maxHp;
        model.setMotherShipId(motherShip.getId())
             .setQuality(motherShip.getMotherShipConfigQualityConfig().getMotherShipQualityConfig().getQuality())
             .setOwner(motherShip.getCommandBuilding().getKosProfile().getUser().getUserProfile().getUsername())
             .setHpAfterBattle(currentHp)
             .setAtk1(kosWarInfoService.getMotherShipPower(motherShip, ShipStatisticType.ATK1).longValue())
             .setAtk2(kosWarInfoService.getMotherShipPower(motherShip, ShipStatisticType.ATK2).longValue())
             .setHp(maxHp)
             .setAmount(1) // default
             .setCurrentHp(currentHp)
             .setKosProfileId(motherShip.getCommandBuilding().getKosProfile().getId())
             .setDef1(def1)
             .setDef2(def2)
             .setBattleProfileId(motherShip.getBattleProfileId())
             .setModelId(motherShip.getMotherShipConfigQualityConfig().getId())
             .setLineupId(motherShip.getLineupId());
        return model;
    }

    public EscortShipBattle toEscortShipBattle(EscortShipSquad escortShipSquad, DefBattleConfig defBattleConfig) {
        EscortShipBattle model = new EscortShipBattle();
        Double dodge = kosWarInfoService.getSingleEscortShipPower(escortShipSquad.getEscortShip(), ShipStatisticType.DODGE);
        Double def1 = kosWarInfoService.getSingleEscortShipPower(escortShipSquad.getEscortShip(), ShipStatisticType.DEF1)
                      * defBattleConfig.getDefPercent() + dodge * defBattleConfig.getDodgePercent();
        Double def2 = kosWarInfoService.getSingleEscortShipPower(escortShipSquad.getEscortShip(), ShipStatisticType.DEF2)
                      * defBattleConfig.getDefPercent() + dodge * defBattleConfig.getDodgePercent();
        Long maxHp = kosWarInfoService.getSingleEscortShipPower(escortShipSquad.getEscortShip(), ShipStatisticType.HP).longValue();
        KosProfile kosProfile = kosProfileService.getByAssetsId(escortShipSquad.getEscortShip().getEscortShipGroup().getAssets().getId());
        model.setEscortShipSquadId(escortShipSquad.getId())
             .setHpLost(escortShipSquad.getHpLost())
             .setEscortShipType(escortShipSquad.getEscortShip().getEscortShipConfig().getType())
             .setGroupName(escortShipSquad.getEscortShip().getEscortShipConfig().getEscortShipGroupConfig().getName())
             .setAtk1(kosWarInfoService.getSingleEscortShipPower(escortShipSquad.getEscortShip(), ShipStatisticType.ATK1).longValue())
             .setAtk2(kosWarInfoService.getSingleEscortShipPower(escortShipSquad.getEscortShip(), ShipStatisticType.ATK2).longValue())
             .setDef1(def1)
             .setDef2(def2)
             .setHp(maxHp)
             .setAmount((int) (escortShipSquad.getAmount() - escortShipSquad.getKilled()))
             .setKosProfileId(kosProfile.getId())
             .setCurrentHp(model.getAmount() * maxHp - escortShipSquad.getHpLost())
             .setBattleProfileId(escortShipSquad.getBattleProfileId())
             .setModelId(escortShipSquad.getEscortShip().getEscortShipConfig().getId())
             .setLineupId(escortShipSquad.getLineupId());
        return model;
    }

    @Transactional
    public BossSeaBattle toBossBattle(BossSea bossSea, DefBattleConfig config) {
        BossSeaBattle model = new BossSeaBattle();
        BossSeaConfig bossSeaConfig = (BossSeaConfig) seaElementConfigRepository.findBossSeaConfigById(bossSea.getSeaElementConfig().getId())
                                                                                .get(); // todo;
        Long currentHp = bossSeaConfig.getBossHp() - bossSea.getHpLost();
        Double dodge = bossSeaConfig.getBossDodge();
        model.setElementId(bossSea.getId())
             .setBossConfigId(bossSea.getSeaElementConfig().getId())
             .setHpAfterBattle(currentHp)
             .setModelId(bossSea.getSeaElementConfig().getId())
             .setAmount(1)
             .setAtk1(bossSeaConfig.getBossAtk1())
             .setAtk2(bossSeaConfig.getBossAtk2())
             .setDef1(bossSeaConfig.getBossDef1() * config.getDefPercent() + dodge * config.getDodgePercent())
             .setDef2(bossSeaConfig.getBossDef2() * config.getDefPercent() + dodge * config.getDodgePercent())
             .setHp(bossSeaConfig.getBossHp())
             .setCurrentHp(currentHp)
             .setBattleProfileId(bossSea.getBattleProfile().getId());

        return model;

    }
}