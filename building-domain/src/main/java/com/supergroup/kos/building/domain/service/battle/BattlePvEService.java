package com.supergroup.kos.building.domain.service.battle;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.core.utils.RoundUtil;
import com.supergroup.kos.building.domain.command.SaveOrUpdateElementCommand;
import com.supergroup.kos.building.domain.command.WithdrawActivityCommand;
import com.supergroup.kos.building.domain.constant.BattleProfileType;
import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.constant.battle.BattleUnitType;
import com.supergroup.kos.building.domain.constant.battle.FactionType;
import com.supergroup.kos.building.domain.constant.battle.ShipType;
import com.supergroup.kos.building.domain.constant.item.ItemId;
import com.supergroup.kos.building.domain.constant.seamap.SeaActivityStatus;
import com.supergroup.kos.building.domain.dto.battle.CreateTaskBattleEvent;
import com.supergroup.kos.building.domain.dto.battle.RevivalBossMessage;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.battle.BattleFinalReport;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;
import com.supergroup.kos.building.domain.model.battle.BattlePvEStatic;
import com.supergroup.kos.building.domain.model.battle.BattleReport;
import com.supergroup.kos.building.domain.model.battle.BattleReward;
import com.supergroup.kos.building.domain.model.battle.BattleRound;
import com.supergroup.kos.building.domain.model.battle.BattleRoundSnapshot;
import com.supergroup.kos.building.domain.model.battle.BattleUnit;
import com.supergroup.kos.building.domain.model.battle.BossReport;
import com.supergroup.kos.building.domain.model.battle.BossSeaBattle;
import com.supergroup.kos.building.domain.model.battle.BossSeaEmbedded;
import com.supergroup.kos.building.domain.model.battle.DamageReport;
import com.supergroup.kos.building.domain.model.battle.EscortShipBattle;
import com.supergroup.kos.building.domain.model.battle.EscortShipReport;
import com.supergroup.kos.building.domain.model.battle.MotherShipBattle;
import com.supergroup.kos.building.domain.model.battle.MotherShipReport;
import com.supergroup.kos.building.domain.model.battle.RoundReport;
import com.supergroup.kos.building.domain.model.battle.RoundUsedItem;
import com.supergroup.kos.building.domain.model.battle.ShipLineUpBattle;
import com.supergroup.kos.building.domain.model.battle.UserBattleReport;
import com.supergroup.kos.building.domain.model.battle.logic.Attack;
import com.supergroup.kos.building.domain.model.battle.logic.AttackDamage;
import com.supergroup.kos.building.domain.model.battle.logic.BattleProgress;
import com.supergroup.kos.building.domain.model.battle.logic.BattleProgressPvEResult;
import com.supergroup.kos.building.domain.model.battle.logic.Belligerent;
import com.supergroup.kos.building.domain.model.config.EscortShipConfig;
import com.supergroup.kos.building.domain.model.config.RelicConfig;
import com.supergroup.kos.building.domain.model.config.WeaponConfig;
import com.supergroup.kos.building.domain.model.config.battle.BattleFieldConfig;
import com.supergroup.kos.building.domain.model.config.battle.BattleFieldElementDamageConfig;
import com.supergroup.kos.building.domain.model.config.battle.BattleFieldLineUpConfig;
import com.supergroup.kos.building.domain.model.config.battle.BattleFiledElementConfig;
import com.supergroup.kos.building.domain.model.config.battle.BattleFiledRowsConfig;
import com.supergroup.kos.building.domain.model.config.battle.BattlefieldDamageConfig;
import com.supergroup.kos.building.domain.model.config.battle.DefBattleConfig;
import com.supergroup.kos.building.domain.model.config.seamap.BossSeaConfig;
import com.supergroup.kos.building.domain.model.item.Item;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.relic.Relic;
import com.supergroup.kos.building.domain.model.seamap.BossSea;
import com.supergroup.kos.building.domain.model.seamap.EscortShipSquad;
import com.supergroup.kos.building.domain.model.seamap.KosWarInfo;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.Ship;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.model.ship.MotherShip;
import com.supergroup.kos.building.domain.model.weapon.Weapon;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleProfileRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRoundRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRoundSnapshotRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.BossReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.EscortShipReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.MotherShipReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.RoundReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.RoundUseItemRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.UserBattleReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.item.ItemRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.BossSeaElementRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.MoveSessionRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementConfigRepository;
import com.supergroup.kos.building.domain.service.config.KosConfigService;
import com.supergroup.kos.building.domain.service.point.PointService;
import com.supergroup.kos.building.domain.service.relic.RelicService;
import com.supergroup.kos.building.domain.service.seamap.BossSeaService;
import com.supergroup.kos.building.domain.service.seamap.EscortShipSquadService;
import com.supergroup.kos.building.domain.service.seamap.KosWarInfoService;
import com.supergroup.kos.building.domain.service.seamap.MapService;
import com.supergroup.kos.building.domain.service.seamap.activity.LineUpService;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;
import com.supergroup.kos.building.domain.service.ship.EscortShipConfigService;
import com.supergroup.kos.building.domain.service.ship.MotherShipService;
import com.supergroup.kos.building.domain.service.weapon.WeaponService;
import com.supergroup.kos.building.domain.task.BattleTask;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BattlePvEService implements BattleHandler{

    public static final String BATTLE_EXCHANGE       = "BATTLE_EXCHANGE";
    public static final String BATTLE_QUEUE          = "BATTLE_QUEUE";
    public static final String REVIVAL_BOSS_EXCHANGE = "REVIVAL_BOSS_EXCHANGE";
    public static final String REVIVAL_BOSS_QUEUE    = "REVIVAL_BOSS_QUEUE";

    @Delegate
    private final BattleRepository              battleRepository;
    private final LineUpService                 lineUpService;
    private final BattleRoundRepository         battleRoundRepository;
    private final BattleFieldConfigService      battleFieldConfigService;
    private final KosConfigService              kosConfigService;
    private final BattleRoundSnapshotRepository battleRoundSnapshotRepository;
    private final BattleUnitService             shipBattleService;
    private final BattleProfileRepository       battleProfileRepository;
    private final BattleReportRepository        battleReportRepository;
    private final RoundReportRepository         roundReportRepository;
    private final MotherShipReportRepository    motherShipReportRepository;
    private final EscortShipReportRepository    escortShipReportRepository;
    private final RoundUseItemRepository        roundUseItemRepository;
    private final MoveSessionRepository         moveSessionRepository;
    private final MapService                    mapService;
    private final EscortShipConfigService       escortShipConfigService;
    private final BattleReportService           battleReportService;
    private final ItemRepository                itemRepository;
    private final BossReportRepository          bossReportRepository;
    private final BattleUnitService             battleUnitService;
    private final MotherShipService             motherShipService;
    private final EscortShipSquadService        escortShipSquadService;
    private final SeaActivityService            seaActivityService;
    private final BattleRewardService           battleRewardService;
    private final UserBattleReportRepository    userBattleReportRepository;
    private final ApplicationEventPublisher     publisher;
    private final BossSeaService                bossSeaService;
    private final RelicService                  relicService;
    private final RabbitTemplate                rabbitTemplate;
    private final WeaponService                 weaponService;
    private final ObjectMapper                  objectMapper;
    private final KosWarInfoService             kosWarInfoService;
    private final SeaElementConfigRepository    seaElementConfigRepository;
    private final BossSeaElementRepository      bossSeaElementRepository;
    private final PointService                  pointService;

    @Transactional
    public Battle startBattle(Battle battle, SeaActivity attacker, BossSea battleField) { //todo change UserBase to SeaElement
        log.info("Start battle {} ", battle.getId());
        LocalDateTime now = LocalDateTime.now();
        // set up time start
        battle.getBattleReport().setStartAt(now);
        battleReportRepository.save(battle.getBattleReport());
        battleField.setBattle(battle);
        battle.setBattleField(battleField);
        // update shipLineUp for battle
        ShipLineUp shipLineUpAttacker = attacker.getLineUp();
        if (Objects.isNull(shipLineUpAttacker) || Objects.isNull(shipLineUpAttacker.getMotherShip())) {
//            onBattleEnded(battle, FactionType.DEFENDER);// todo
            return null;
        }
        shipLineUpAttacker.setTimeJoinedBattle(now).setBattleProfile(battle.getAttacker());
        lineUpService.save(shipLineUpAttacker);
        List<ShipLineUp> lineUps = new ArrayList<>();
        lineUps.add(shipLineUpAttacker);
        BattleProfile battleProfileAttacker = battle.getAttacker();
        battleProfileAttacker.setShipLineUps(lineUps);
        battleProfileRepository.save(battleProfileAttacker);
        createShipLineUpForDefender(battle.getDefender(), now, battleField);
        beforeBattleProgress(battle);
        battleRepository.save(battle);
        mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(battleField));
        updateAllBattleActivityTimer(battle, LocalDateTime.now().plus(kosConfigService.getBattleTimeConfig().getMonster().getProgressDuration(),
                                                                      ChronoUnit.SECONDS), SeaActivityStatus.ATTACKING);
        publisher.publishEvent(new CreateTaskBattleEvent(battle));
        return battle;
    }

    @Transactional
    public void createShipLineUpForDefender(BattleProfile battleProfile, LocalDateTime now, BossSea bossSea) {
        // todo
        List<ShipLineUp> shipLineUps = new ArrayList<>();
        BossSeaEmbedded bossSeaEmbedded = new BossSeaEmbedded().setId(bossSea.getId())
                                                               .setConfigId(bossSea.getSeaElementConfig().getId());
        ShipLineUp shipLineUp = new ShipLineUp().setBattleProfile(battleProfile)
                                                .setTimeJoinedBattle(now)
                                                .setBossSea(bossSeaEmbedded);
        shipLineUps.add(shipLineUp);
        shipLineUp.setBattleProfile(battleProfile).setTimeJoinedBattle(now);

        battleProfile.setShipLineUps(lineUpService.saveAll(shipLineUps));
    }

    private void updateAllBattleActivityTimer(Battle battle, LocalDateTime timeEnd, SeaActivityStatus status) {
        for (BattleProfile battleProfile : battle.getBattleProfiles()) {
            if (battleProfile.getType().equals(BattleProfileType.BOSS)) {
                continue;
            }
            if (battle.getDefender().getType().equals(BattleProfileType.USER)
                && battleProfile.getKosProfile().getId().equals(
                    battle.getDefender().getKosProfile().getId())) { // defense mothership is not an activity
                continue;
            }
            List<SeaActivity> activities = battleProfile.getShipLineUps().stream().map(ShipLineUp::getActivity).collect(Collectors.toList());
            for (SeaActivity activity : activities) {
                seaActivityService.updateCurrentActionStatus(activity, timeEnd, status);
            }
        }
    }

    public void sendBattleTaskToQueue(Battle battle) throws JsonProcessingException {
        if (BattleStatus.END.equals(battle.getStatus())) {
            return;
        }
        var duration = kosConfigService.getBattleTimeConfig().getMonster().getProgressDuration() * 1000; // TODO this is hard code
        var task = new BattleTask().setBattleId(battle.getId())
                                   .setDefenderName(battle.getDefender().getType().name())
                                   .setBattleType(battle.getBattleType());
        log.info("Battle:{} sent to queue, duration:{}", task.getBattleId(), duration);
        ObjectMapper objectMapper = new ObjectMapper();
        var taskJson = objectMapper.writeValueAsString(task);
        var prop = new MessageProperties();
        prop.setHeader("x-delay", duration);
        var mess = MessageBuilder.withBody(taskJson.getBytes())
                                 .andProperties(prop)
                                 .build();
        rabbitTemplate.convertAndSend(BATTLE_EXCHANGE, BATTLE_QUEUE, mess);
    }

    public void sendRevivalTaskToQueue(BossSea bossSea) throws JsonProcessingException {
        var bossSeaJson = objectMapper.writeValueAsString(new RevivalBossMessage().setId(bossSea.getId()));
        var prop = new MessageProperties();
        var duration = Math.abs(Duration.between(LocalDateTime.now(), bossSea.getTimeRevivingEnd()).toMillis());
        prop.setHeader("x-delay", duration);
        var mess = MessageBuilder.withBody(bossSeaJson.getBytes())
                                 .andProperties(prop)
                                 .build();
        rabbitTemplate.convertAndSend(REVIVAL_BOSS_EXCHANGE, REVIVAL_BOSS_QUEUE, mess);
    }

    private void beforeBattleProgress(Battle battle) {
        Long currentRound = battle.getCurrentRound();
        BattleRound battleRound = new BattleRound().setIndex(currentRound + 1).setBattle(battle);
        battle.setCurrentRound(currentRound + 1).setStatus(BattleStatus.PROGRESS);
        // save to db
        battleRound = battleRoundRepository.save(battleRound);
        ShipLineUpBattle shipLineUpBattle = getListShipLineUpInBattle(battle);
        BattleRoundSnapshot snapshot = createBattleSnapshot(shipLineUpBattle, battleRound);
//        updateFlatCheckResultBattle(battle, snapshot);
        battle = battleRepository.save(battle);
        // todo start
    }

    @Transactional
    private ShipLineUpBattle getListShipLineUpInBattle(Battle battle) {
        List<BattleProfile> battleProfileList = battle.getBattleProfiles();
        List<BattleProfile> attacker = new ArrayList<>();
        List<BattleProfile> defender = new ArrayList<>();
        for (BattleProfile battleProfile : battleProfileList) {
            if (battleProfile.getFaction().equals(FactionType.ATTACKER)) {
                attacker.add(battleProfile);
            } else {
                defender.add(battleProfile);
            }
        }
        List<ShipLineUp> shipLineUpsAttacker = new ArrayList<>();
        List<ShipLineUp> shipLineUpsDefender = new ArrayList<>();
        for (BattleProfile battleProfile : attacker) {

            shipLineUpsAttacker.addAll(battleProfile.getShipLineUps());
        }
        for (BattleProfile battleProfile : defender) {
            shipLineUpsDefender.addAll(battleProfile.getShipLineUps());
        }
        return new ShipLineUpBattle().setShipLineupAttacker(shipLineUpsAttacker)
                                     .setShipLineupDefender(shipLineUpsDefender);
    }

    private BattleRoundSnapshot createBattleSnapshot(ShipLineUpBattle shipLineUpBattle, BattleRound battleRound) {
        // todo map Ship
        List<ShipLineUp> shipLineUpAttacker = shipLineUpBattle.getShipLineupAttacker();
        List<ShipLineUp> shipLineUpDefender = shipLineUpBattle.getShipLineupDefender();

        // to mapShip and filter ship
        Map<String, List<Ship>> mapShipAttacker = toMapShip(shipLineUpAttacker);
        List<BossSea> bossSeas = getListBoss(shipLineUpDefender);

        // create attacker, defender
        Attack attacker = new Attack();
        Attack defender = new Attack();

        // create belligerent joined
        createBelligerentJoined(attacker, shipLineUpAttacker);
        createBelligerentJoined(defender, shipLineUpDefender);

        // built battlefield for attack
        createBattleFieldForAttack(attacker, mapShipAttacker);
        createBattleFieldForDefender(defender, bossSeas);

        // create snapshot
        BattleRoundSnapshot snapshot = new BattleRoundSnapshot().setBattleRound(battleRound)
                                                                .setAttackerModel(attacker)
                                                                .setDefenderModel(defender);
        battleRound.setBattleRoundSnapshot(snapshot);

        // save to db
//        battleRoundRepository.save(battleRound);// check todo
        return battleRoundSnapshotRepository.save(snapshot);
    }

    private void createBelligerentJoined(Attack attack, List<ShipLineUp> shipLineUps) {
        Map<Long, Belligerent> belligerentJoined = new HashMap<>();
        for (ShipLineUp lineUp : shipLineUps) {
            BattleProfile battleProfile = lineUp.getBattleProfile();
            if (Objects.nonNull(battleProfile)) {
                if (!belligerentJoined.containsKey(battleProfile.getId())) {
                    Belligerent belligerent = new Belligerent().setBattleProfileId(battleProfile.getId())
                                                               .setKosProfileId(Objects.nonNull(battleProfile.getKosProfile()) ?
                                                                                battleProfile.getKosProfile().getId() : null); // case boss is null
                    belligerentJoined.put(battleProfile.getId(), belligerent);
                }
            } else {
                log.info("Battle warning : battleProfile in shipLineup null");
            }
        }
        attack.setBelligerentJoined(belligerentJoined);
    }

    private Attack createBattleFieldForAttack(Attack attack, Map<String, List<Ship>> maps) {
        DefBattleConfig defBattleConfig = kosConfigService.getDefBattleConfig();
        Map<String, List<BattleUnit>> mapsShipBattle = new HashMap<>();
        Set<String> keys = maps.keySet();
        for (String key : keys) {
            List<BattleUnit> shipBattleList = new ArrayList<>();
            mapsShipBattle.put(key, shipBattleList);
            List<Ship> ships = maps.get(key);
            for (Ship ship : ships) {
                if (ship instanceof MotherShip) {
                    MotherShip motherShip = (MotherShip) ship;
                    shipBattleList.add(shipBattleService.toMotherShipBattle(motherShip, defBattleConfig));
                } else if (ship instanceof EscortShipSquad) {
                    EscortShipSquad escortShipSquad = (EscortShipSquad) ship;
                    shipBattleList.add(shipBattleService.toEscortShipBattle(escortShipSquad, defBattleConfig));

                }
            }
        }
        return attack.setBattleFields(mapsShipBattle);
    }

    private Attack createBattleFieldForDefender(Attack attack, List<BossSea> bossSeas) {
        DefBattleConfig config = kosConfigService.getDefBattleConfig();
        Map<String, List<BattleUnit>> mapsShipBattle = new HashMap<>();
        List<BattleUnit> battleUnits = new ArrayList<>();
        for (BossSea bossSea : bossSeas) {
            BattleUnit battleUnit = battleUnitService.toBossBattle(bossSea, config);
            battleUnits.add(battleUnit);
        }
        mapsShipBattle.put(builtKeyBattleUnit(BattleUnitType.BOSS, null), battleUnits);
        attack.setBattleFields(mapsShipBattle);
        return attack;
    }

    private Map<String, List<Ship>> toMapShip(List<ShipLineUp> shipLineUps) {
        Map<String, List<Ship>> result = new HashMap<>();

        // init maps ship with motherShip and all type EscortShipType
        List<Ship> listMotherShip = new ArrayList<>();
        result.put(builtKeyShip(ShipType.MOTHER_SHIP, null), listMotherShip);
        for (EscortShipType escortShipType : EscortShipType.values()) {
            List<Ship> listEscortShip = new ArrayList<>();
            result.put(builtKeyShip(ShipType.ESCORT_SHIP, escortShipType), listEscortShip);
        }

        List<Ship> motherShipList = result.get(builtKeyShip(ShipType.MOTHER_SHIP, null));
        for (ShipLineUp shipLineUp : shipLineUps) {
            Long battleProfileId = Objects.nonNull(shipLineUp.getBattleProfile()) ? shipLineUp.getBattleProfile().getId() : null;
            MotherShip motherShip = shipLineUp.getMotherShip();
            if (motherShip != null && motherShip.getCurrentHp() > 0) {
                motherShip.setBattleProfileId(battleProfileId)
                          .setLineupId(shipLineUp.getId());
                motherShipList.add(motherShip);
            }
            if (Objects.isNull(shipLineUp.getEscortShipSquad())) {continue;}
            var escortShipSquads = shipLineUp.getEscortShipSquad()
                                             .stream()
                                             .filter(is -> (is.getAmount() > is.getKilled()))
                                             .collect(Collectors.toList());
            for (EscortShipSquad escortShipSquad : escortShipSquads) {
                escortShipSquad.setBattleProfileId(battleProfileId)
                               .setLineupId(shipLineUp.getId());
                String key = builtKeyShip(ShipType.ESCORT_SHIP, escortShipSquad.getEscortShip().getEscortShipConfig().getType());
                if (result.containsKey(key)) {
                    List<Ship> escortShipSquadList1 = result.get(key);
                    escortShipSquadList1.add(escortShipSquad);
                } else {
                    List<Ship> escortShipSquadList1 = new ArrayList<>();
                    escortShipSquadList1.add(escortShipSquad);
                    result.put(key, escortShipSquadList1);
                }
            }
        }
        return result;
    }

    List<BossSea> getListBoss(List<ShipLineUp> shipLineUps) {
        Collection<Long> idsBossSea = new ArrayList<>();
        Map<Long, BattleProfile> maps = new HashMap<>();
        for (ShipLineUp shipLineUp : shipLineUps) {
            if (shipLineUp.getBossSea().getId() != null) {
                idsBossSea.add(shipLineUp.getBossSea().getId());
                maps.put(shipLineUp.getBossSea().getId(), shipLineUp.getBattleProfile());
            }
        }
        List<BossSea> result = bossSeaElementRepository.findByIdIn(idsBossSea);
        for (BossSea bossSea : result) {
            bossSea.setBattleProfile(maps.get(bossSea.getId()));
        }
        return result;
    }

    public String builtKeyBattleUnit(BattleUnitType battleUnitType, EscortShipType escortShipType) {
        if (battleUnitType.equals(BattleUnitType.MOTHER_SHIP) || battleUnitType.equals(BattleUnitType.BOSS)) {
            return battleUnitType.name().toString();
        }
        return battleUnitType.name().toString() + ":" + escortShipType.name().toString();
    }

    public String builtKeyShip(ShipType shipType, EscortShipType escortShipType) {
        if (shipType.equals(ShipType.MOTHER_SHIP)) {
            return shipType.name().toString();
        }
        return shipType.name().toString() + ":" + escortShipType.name().toString();
    }

    @Transactional
    public void changeStatusBattle(BattleTask battleTask) {
        Long battleId = battleTask.getBattleId();
        Optional<Battle> optional = battleRepository.findById(battleId);
        if (optional.isEmpty() || BattleStatus.END.equals(optional.get().getStatus())) {
            return;
        }
        Battle battle = optional.get();
        // todo check
        // end
        BattleRound round = getCurrentRoundBattle(battle);
        BattleRoundSnapshot snapshot = round.getBattleRoundSnapshot();
        Attack attacker = snapshot.getAttackerModel();
        Attack defender = snapshot.getDefenderModel();

        // valid battle report
        validateBattleReport(battle.getBattleProfiles(), battle.getBattleReport());

        // battle progress
        BattleProgress battleProgress = new BattleProgress().setAttacker(attacker).setDefender(defender);
        FactionType factionType = battleProgress(battleProgress).getFactionTypeWin();

        // update to database
        updateShipToDatabase(attacker.getBattleFields());
        updateBossToDatabase(defender.getBattleFields());

        RoundReport roundReport = new RoundReport().setRound(round)
                                                   .setBattleReport(round.getBattle().getBattleReport());
        roundReport = roundReportRepository.save(roundReport);
        roundReport = updateRoundReport(roundReport, attacker, defender);
        round.setReport(roundReport);
        round = battleRoundRepository.save(round);
        snapshot = battleRoundSnapshotRepository.save(snapshot.setCurrentRound(battle.getCurrentRound()));
        log.info("Battle :{} Round - {}; Snapshot - {}", battle.getId(), round.getId(), snapshot.getId());

        // update WarInfo after Round
        staticWarInfoAfterRound(snapshot);
        onBattleEnded(battle, factionType);
    }

    private void updateBossToDatabase(Map<String, List<BattleUnit>> maps) {
        String key = builtKeyBattleUnit(BattleUnitType.BOSS, null);
        List<BattleUnit> battleUnitList = maps.getOrDefault(key, new ArrayList<>());
        for (BattleUnit battleUnit : battleUnitList) {
            BossSeaBattle bossSeaBattle = (BossSeaBattle) battleUnit;
            bossSeaService.updateHp(bossSeaBattle.getElementId(), bossSeaBattle.getCurrentHp());
        }
    }

    private void updateShipToDatabase(Map<String, List<BattleUnit>> maps) {
        List<BattleUnit> shipBattles = new ArrayList<>();
        for (Entry<String, List<BattleUnit>> entry : maps.entrySet()) {
            List<BattleUnit> shipBattleTookDamage = entry.getValue().stream().filter(is -> is.getIsTookDamage().equals(true)).collect(
                    Collectors.toList());
            shipBattles.addAll(shipBattleTookDamage);
        }
        List<Long> idsMotherShip = shipBattles.stream()
                                              .filter(v -> (v instanceof MotherShipBattle))
                                              .map(v -> (MotherShipBattle) v)
                                              .map(MotherShipBattle::getMotherShipId)
                                              .collect(Collectors.toList());
        List<Long> idsEscortShip = shipBattles.stream()
                                              .filter(v -> (v instanceof EscortShipBattle))
                                              .map(v -> (EscortShipBattle) v)
                                              .map(EscortShipBattle::getEscortShipSquadId)
                                              .collect(Collectors.toList());
        List<MotherShip> motherShipList = motherShipService.findByIdIn(idsMotherShip);
        List<EscortShipSquad> escortShipSquadList = escortShipSquadService.finByIdIn(idsEscortShip);

        for (MotherShip motherShip : motherShipList) {
            var optional = shipBattles.stream()
                                      .filter(v -> (v instanceof MotherShipBattle))
                                      .map(v -> (MotherShipBattle) v)
                                      .filter(v -> v.getMotherShipId().equals(motherShip.getId()))
                                      .findFirst();
            optional.ifPresent(shipBattle -> updateMotherShipToDataBase(motherShip, shipBattle));
        }
        for (EscortShipSquad escortShipSquad : escortShipSquadList) {
            var optional = shipBattles.stream()
                                      .filter(v -> (v instanceof EscortShipBattle))
                                      .map(v -> (EscortShipBattle) v)
                                      .filter(v -> v.getEscortShipSquadId().equals(escortShipSquad.getId()))
                                      .findFirst();
            optional.ifPresent(shipBattle -> updateEscortShipToDataBase(escortShipSquad, shipBattle));
        }

        motherShipService.saveAll(motherShipList);
        escortShipSquadService.saveAll(escortShipSquadList);
    }

    private MotherShip updateMotherShipToDataBase(MotherShip motherShip, MotherShipBattle motherShipBattle) {
        // update status if have status for mother ship todo
        motherShip.setCurrentHp(motherShipBattle.getCurrentHp());
        return motherShip;
    }

    private EscortShipSquad updateEscortShipToDataBase(EscortShipSquad escortShipSquad, EscortShipBattle escortShipBattle) {
        escortShipSquad.setKilled(escortShipSquad.getKilled() + escortShipBattle.getKilled());
        escortShipSquad.setHpLost(escortShipBattle.getHpDebt());
        return escortShipSquad;
    }

    private RoundReport updateRoundReport(RoundReport roundReport, Attack attacker, Attack defender) {
        Map<Long, BattleProfile> maps = getMapBattleProfile(attacker, defender);
        // damage report
        roundReport.setAttackerDamageReport(createDamageReport(attacker))
                   .setDefenderDamageReport(createDamageReport(defender));

        // create mother ship report
        createMotherShipReport(attacker, maps, roundReport, FactionType.ATTACKER);

        // create escort ship report and reverse mother ship report
        createEscortShipReport(attacker, roundReport, FactionType.ATTACKER);

        // create AttackUseItem
        createAttackUserItem(attacker, maps, roundReport, FactionType.ATTACKER);

        // create boss report
        createBossReport(defender, maps, roundReport);

        return roundReportRepository.save(roundReport);
    }

    private void createBossReport(Attack attack, Map<Long, BattleProfile> maps, RoundReport roundReport) {
        String key = builtKeyBattleUnit(BattleUnitType.BOSS, null);
        List<BattleUnit> list = attack.getBattleFields().getOrDefault(key, new ArrayList<>());
        List<BossReport> bossReportList = new ArrayList<>();
        for (BattleUnit battleUnit : list) {
            BossSeaBattle bossSeaBattle = (BossSeaBattle) battleUnit;
            BossReport bossReport = new BossReport();
            bossReport.setBattleProfile(maps.get(bossSeaBattle.getBattleProfileId()))
                      .setCurrentHp(bossSeaBattle.getCurrentHp())
                      .setHpLost(bossSeaBattle.getHpAfterBattle() - bossSeaBattle.getCurrentHp())
                      .setMaxHp(bossSeaBattle.getHp())
                      .setModelId(bossSeaBattle.getModelId())
                      .setRoundReport(roundReport)
                      .setFaction(FactionType.DEFENDER);

            bossReportList.add(bossReport);
        }
        bossReportRepository.saveAll(bossReportList);
    }

    private Map<Long, BattleProfile> getMapBattleProfile(Attack attacker, Attack defender) {
        Map<Long, BattleProfile> maps = new HashMap<>();
        List<Long> idsBattleProfile = new ArrayList<>();
        idsBattleProfile.addAll(attacker.getBelligerentJoined().keySet());
        idsBattleProfile.addAll(defender.getBelligerentJoined().keySet());
        Set<BattleProfile> battleProfileList = battleProfileRepository.findByIdIn(idsBattleProfile);
        for (BattleProfile battleProfile : battleProfileList) {
            maps.put(battleProfile.getId(), battleProfile);
        }
        return maps;
    }

    private void createAttackUserItem(Attack attack, Map<Long, BattleProfile> maps, RoundReport roundReport, FactionType factionType) {
        List<RoundUsedItem> roundUsedItems = new ArrayList<>();
        Map<Long, Belligerent> belligerentJoined = attack.getBelligerentJoined();
        for (Entry<Long, Belligerent> entry : belligerentJoined.entrySet()) {
            RoundUsedItem roundUsedItem = new RoundUsedItem();
            roundUsedItem.setRoundReport(roundReport)
                         .setBattleProfile(maps.get(entry.getKey()))
                         .setItems(getListItemById(entry.getValue().getItemIds()))
                         .setFaction(factionType);
            roundUsedItems.add(roundUsedItem);
        }
        roundUseItemRepository.saveAll(roundUsedItems);

    }

    private List<Item> getListItemById(List<ItemId> ids) {
        if (Objects.nonNull(ids)) {
            return itemRepository.findByIdIn(ids);
        }
        return null;
    }

    private void createEscortShipReport(Attack attack, RoundReport roundReport, FactionType factionType) {
        // todo update
        List<EscortShipConfig> escortShipConfigs = escortShipConfigService.getAll();
        List<EscortShipReport> escortShipReports = new ArrayList<>();
        for (var config : escortShipConfigs) {
            String key = builtKeyShip(ShipType.ESCORT_SHIP, config.getType());
            if (attack.getBattleFields().containsKey(key)) {
                List<BattleUnit> shipBattleList = attack.getBattleFields().get(key);

                var totalShipFighting = 0L;
                var totalShipKilled = 0L;
                var modelId = config.getId();
                var shipGroupName = config.getEscortShipGroupConfig().getName();
                var escortShipType = config.getType();

                if (!shipBattleList.isEmpty()) {
                    for (BattleUnit shipBattle : shipBattleList) {
                        totalShipFighting += shipBattle.getFighting();
                        totalShipKilled += shipBattle.getKilled();
                    }
                }

                // create escortShipReport
                EscortShipReport escortShipReport = new EscortShipReport(factionType);
                escortShipReport.setEscortShipType(escortShipType)
                                .setEscortShipGroupName(shipGroupName)
                                .setLeft(totalShipFighting - totalShipKilled)
                                .setLost(totalShipKilled)
                                .setAdd(0L)
                                .setModelId(modelId)
                                .setRoundReport(roundReport);
                escortShipReports.add(escortShipReport);
            }
        }
        escortShipReportRepository.saveAll(escortShipReports);
    }

    private DamageReport createDamageReport(Attack attack) {
        DamageReport damageReport = new DamageReport();
        damageReport.setEscortShip(attack.getTotalEscortShipJoined())
                    .setMotherShip(attack.getTotalMotherShipJoined())
                    .setPhysicalAttack(attack.getTotalAtk1Dealt())
                    .setFirePower(attack.getTotalAtk2Dealt())
                    .setTakenPhysicalAttack(attack.getTotalAtk1Taken())
                    .setTakenFirePower(attack.getTotalAtk2Taken())
                    .setEscortShipLost(attack.getTotalEscortShipKilled())
                    .setMotherShipLost(attack.getTotalMotherShipLost())
                    .setNpcLostHp(attack.getNpcLostHp());
        damageReport.setTotalMotherShipJoined(attack.getTotalMotherShipJoined())
                    .setTotalEscortShipJoined(attack.getTotalEscortShipJoined())
                    .setTotalMotherShipLost(attack.getTotalMotherShipLost())
                    .setTotalEscortShipKilled(attack.getTotalEscortShipKilled())
                    .setTotalHpMotherShipLost(attack.getTotalHpMotherShipLost())
                    .setTotalAtk1Dealt(attack.getTotalAtk1Dealt())
                    .setTotalAtk2Dealt(attack.getTotalAtk2Dealt())
                    .setTotalAtk1Taken(attack.getTotalAtk1Taken())
                    .setTotalAtk2Taken(attack.getTotalAtk2Taken());
        return damageReport;

    }

    private void createMotherShipReport(Attack attack, Map<Long, BattleProfile> maps, RoundReport roundReport, FactionType factionType) {
        List<MotherShipReport> motherShipReports = new ArrayList<>();
        String key = builtKeyShip(ShipType.MOTHER_SHIP, null);
        if (attack.getBattleFields().containsKey(key)) {
            List<BattleUnit> motherShip = attack.getBattleFields().get(key);
            if (!motherShip.isEmpty()) {
                long index = 0;
                for (BattleUnit shipBattle : motherShip) {
                    MotherShipBattle motherShipBattle = (MotherShipBattle) shipBattle;
                    if (shipBattle.getFighting() > 0) {
                        MotherShipReport motherShipReport = new MotherShipReport(factionType);
                        motherShipReport.setIndex(index++)
                                        .setQuality(motherShipBattle.getQuality())
                                        .setBattleProfile(maps.get(shipBattle.getBattleProfileId()))
                                        .setHpLost(motherShipBattle.getHpAfterBattle() - motherShipBattle.getCurrentHp())
                                        .setCurrentHp(motherShipBattle.getCurrentHp())
                                        .setMaxHp(shipBattle.getHp())
                                        .setModelId(motherShipBattle.getModelId())
                                        .setRoundReport(roundReport);
                        motherShipReports.add(motherShipReport);
                    }

                }

            }
        }
        motherShipReportRepository.saveAll(motherShipReports);
    }

    @Transactional
    public BattleProgressPvEResult battleProgress(BattleProgress battleProgress) {
        BattleProgressPvEResult result = new BattleProgressPvEResult();
        Attack attacker = battleProgress.getAttacker();
        Attack defender = battleProgress.getDefender();
        log.info("Battle progress {}", attacker.toString(), defender.toString());
        BattleFieldLineUpConfig battleFieldLineUpConfig = chooseBattleField(attacker, defender);

        // calculate ship : fighting, reserve
        settleLineup(attacker);
        settleLineup(defender);

        BattlePvEStatic staticAttacker = new BattlePvEStatic();
        BattlePvEStatic staticDefender = new BattlePvEStatic();

        // until one side wins
        long maxRound = 1000L; // hard code , todo or get from config or = Integer.MAX_VALUE
        for (long i = 1; i < maxRound; i++) {
            // for test
            battleProgress.setRound(i);
            // calculate total atk
            calculateTotalAtkAttack(attacker);
            calculateTotalAtkAttack(defender);

            // total atk dealt
            battlePveStatic(staticAttacker, attacker);
            battlePveStatic(staticDefender, defender);

            // taken dame
            calculateAfterBattleForDefender(attacker, defender);
            calculateAfterBattle(defender, attacker, battleFieldLineUpConfig);

            //check win
            FactionType factionType = checkFactionWinBattle(attacker, defender);

            // for test
            result.getBattleProgresses().add(SerializationUtils.clone(battleProgress));
//            result.getBattleProgresses().add(battleProgress.);

            if (Objects.nonNull(factionType)) {
                attacker.setTotalAtk1Taken(staticDefender.getTotalAtk1Dealt())
                        .setTotalAtk2Taken(staticDefender.getTotalAtk2Dealt())
                        .setTotalAtk1Dealt(staticAttacker.getTotalAtk1Dealt())
                        .setTotalAtk2Dealt(staticAttacker.getTotalAtk2Dealt());

                defender.setTotalAtk1Taken(staticAttacker.getTotalAtk1Dealt())
                        .setTotalAtk2Taken(staticAttacker.getTotalAtk2Dealt())
                        .setTotalAtk1Dealt(staticDefender.getTotalAtk1Dealt())
                        .setTotalAtk2Dealt(staticDefender.getTotalAtk2Dealt());

                staticAttack(attacker);
                staticAttack(defender);
                return result.setFactionTypeWin(factionType);
            }
        }
        return result.setFactionTypeWin(FactionType.DEFENDER);
    }

    @Transactional
    public void onBattleEnded(Battle battle, FactionType winner) {
        log.info("Battle:{},on end battle in round: {} , win {} ", battle.getId(), battle.getCurrentRound(), winner);
        if (BattleStatus.END.equals(battle.getStatus())) {
            return;
        }
        updateWinnerBattle(battle, winner);

        calculateBattleReward(battle, winner);

        battle.getBattleReport().setWinner(battle.getWinner());
        battle.getBattleReport().setLoser(winner.equals(FactionType.ATTACKER) ? battle.getDefender() : battle.getAttacker());

        battle.getBattleReport().setEndAt(LocalDateTime.now());

        calculateBattleFinalReport(battle);

        log.info("Save and withdraw");
        battleReportRepository.save(battle.getBattleReport());
        SeaElement battleField = battle.getBattleField();
        battle = battleRepository.save(battle);
        withdrawAllLineUp(battle);
        // send notification
        log.info("Send battle report");
        battleReportService.sendBattleReport(battle.getBattleReport());

        log.info("Update sea map element : pve");
        updateBattleField(battleField);

        // update warInfo AfterBattle
        updateWarInfoAfterBattle(battle);
    }

    private void updateBattleField(SeaElement battleField) {
        battleField.setBattle(null);
        if (Objects.nonNull(battleField.getDeleted()) && battleField.getDeleted()) {
            mapService.deleteElement(battleField);
        } else {
            mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(battleField));
        }
    }

    private void updateWinnerBattle(Battle battle, FactionType factionTypeWin) {
        switch (factionTypeWin) {
            case ATTACKER:
                battle.setWinner(battle.getAttacker());
                break;
            case DEFENDER:
                battle.setWinner(battle.getDefender());
                break;
            default:
                break;
        }
        battle.setStatus(BattleStatus.END);
    }

    private void withdrawAllLineUp(Battle battle) {
        var participants = battle.getBattleProfiles()
                                 .stream()
                                 .filter(battleProfile -> !battleProfile.getId().equals(battle.getDefender().getId()))
                                 .collect(Collectors.toList());
        for (BattleProfile participant : participants) {
            if (participant.getType().equals(BattleProfileType.BOSS)) {
                continue;
            }
            withdrawUserAllLineUp(participant.getKosProfile(), battle);
        }

    }

    private boolean withdrawUserAllLineUp(KosProfile kosProfile, Battle battle) {
        Collection<MotherShip> motherShips = getMotherShipJoinInBattle(kosProfile, battle);
        return withdrawListMotherShips(motherShips);
    }

    public List<MotherShip> getMotherShipJoinInBattle(KosProfile kosProfile, Battle battle) {
        var nullableBattleProfile = battle.getBattleProfiles()
                                          .stream()
                                          .filter(v -> v.getType().equals(BattleProfileType.USER))
                                          .filter(profile -> profile.getKosProfile().getId().equals(kosProfile.getId())).findFirst();
        if (nullableBattleProfile.isEmpty()) {
            throw KOSException.of(ErrorCode.USER_HAS_YET_TO_JOIN_BATTLE);
        }
        BattleProfile battleProfile = nullableBattleProfile.get();
        Collection<ShipLineUp> lineUps = battleProfile.getShipLineUps();
        List<MotherShip> motherShips = lineUps.stream().map(ShipLineUp::getMotherShip).collect(Collectors.toList());
        return motherShips;
    }

    private boolean withdrawListMotherShips(Collection<MotherShip> motherShips) {
        boolean canWithdrawAll = true;
        for (MotherShip motherShip : motherShips) {
            try {
                seaActivityService.withdraw(new WithdrawActivityCommand().setId(motherShip.getActiveLineUp().getActivity().getId()));
            } catch (KOSException e) {
                e.printStackTrace();
                canWithdrawAll = false;
            }
        }
        return canWithdrawAll;
    }

    private Boolean isLucky(Double rate) {
        return Math.abs(new Random().nextInt(1000)) / 1000.0 <= rate;
    }

    private void calculateBattleReward(Battle battle, FactionType winner) {

        if (winner.equals(FactionType.DEFENDER)) {
            var reward = new BattleReward().setBattleReport(battle.getBattleReport());
            battle.getBattleReport().setReward(battleRewardService.save(reward));
            return;
        }
        Optional<BossSeaConfig> optional = seaElementConfigRepository.findBossSeaConfigById(battle.getDefender().getBossSea().getConfigId());
        if (optional.isEmpty()) {
            return;
        }
        var bossConfig = optional.get();
        var willGet = bossConfig.getWillGet();
        var mayGet = bossConfig.getMayGet();

        // calculate will get
        var gold = willGet.getGold();
        var wood = willGet.getWood();
        var stone = willGet.getStone();
        var gloryPoint = willGet.getGp();

        // calculate may get
        // relic
        var relics = new ArrayList<Relic>();
        if (isLucky(mayGet.getRelic().getRate())) {
            Collections.shuffle(mayGet.getRelic().getItems());
            for (int i = 0; i < Math.min(mayGet.getRelic().getAmount(), mayGet.getRelic().getItems().size()); i++) {

                var relicId = Long.valueOf(mayGet.getRelic().getItems().get(i));
                // check relic is active
                // if relic is deactivated, ignore
                if (!relicService.isActive(relicId)) {
                    continue;
                }

                var r = new Relic();
                r.setIsListing(false);
                r.setRelicConfig(new RelicConfig().setId(relicId));
                relics.add(r);
            }
        }
        // weapon
        var weapons = new ArrayList<Weapon>();
        if (isLucky(mayGet.getWeapon().getRate())) {
            Collections.shuffle(mayGet.getWeapon().getItems());
            for (int i = 0; i < Math.min(mayGet.getWeapon().getAmount(), mayGet.getWeapon().getItems().size()); i++) {

                var weaponId = Long.valueOf(mayGet.getWeapon().getItems().get(i));
                // check weapon is active
                // if weapon is deactivated, ignore
                if (!weaponService.isActive(weaponId)) {
                    continue;
                }

                var w = new Weapon();
                w.setWeaponConfig(new WeaponConfig().setId(weaponId));
                weapons.add(w);
            }
        }
        // items
        var items = new ArrayList<Item>();
        if (isLucky(mayGet.getItem().getRate())) {
            Collections.shuffle(mayGet.getItem().getItems());
            for (int i = 0; i < Math.min(mayGet.getItem().getAmount(), mayGet.getItem().getItems().size()); i++) {

                var itemId = ItemId.valueOf(mayGet.getItem().getItems().get(i));
                // check item is active
                // if weapon is deactivated, ignore
                if (!itemRepository.isActive(itemId)) {
                    continue;
                }

                var it = new Item();
                it.setId(itemId);
                items.add(it);
            }
        }

        relicService.saveAll(relics);
        weaponService.saveAll(weapons);

        // update gp
        pointService.updateGp(battle.getWinner().getKosProfile(), gloryPoint);

        var battleReward = new BattleReward().setBattleReport(battle.getBattleReport())
                                             .setGold(gold.doubleValue())
                                             .setGoldRemaining(gold.doubleValue())
                                             .setWood(wood.doubleValue())
                                             .setWoodRemaining(wood.doubleValue())
                                             .setStone(stone.doubleValue())
                                             .setStoneRemaining(stone.doubleValue())
                                             .setGloryPoint(gloryPoint.doubleValue())
                                             .setWeapons(weapons)
                                             .setItems(items)
                                             .setRelics(relics);
        var reward = battleRewardService.save(battleReward);
        battle.getBattleReport().setReward(reward);
    }

    private void calculateBattleFinalReport(Battle battle) {
        var attackerFinalReport = new BattleFinalReport();
        var defenderFinalReport = new BattleFinalReport();
        var battleRounds = battle.getBattleRounds();
        Long dem = 0L;
        for (BattleRound round : battleRounds) {
            if (Objects.nonNull(round.getReport())) {
                dem += 1;
                RoundReport roundReport = round.getReport();
                createFinalReport(attackerFinalReport, roundReport.getAttackerDamageReport());
                createFinalReport(defenderFinalReport, roundReport.getDefenderDamageReport());
            }

        }
        log.info("Battle:{}, final report, total report1:{}, total report2:{}", battle.getId(), battleRounds.size(), dem);
        attackerFinalReport.setAmountItem(0L);
        defenderFinalReport.setAmountItem(0L);

        attackerFinalReport.setAmountAlly(battle.getBattleProfiles().stream().filter(b -> FactionType.ATTACKER.equals(b.getFaction())).count());
        defenderFinalReport.setAmountAlly(battle.getBattleProfiles().stream().filter(b -> FactionType.DEFENDER.equals(b.getFaction())).count());

        battle.getBattleReport().setAttackerFinalReport(attackerFinalReport);
        battle.getBattleReport().setDefenderFinalReport(defenderFinalReport);
    }

    private BattleFinalReport createFinalReport(BattleFinalReport battleFinalReport, DamageReport damageReportRound) {
        battleFinalReport.setTotalAtk1(battleFinalReport.getTotalAtk1() + damageReportRound.getTotalAtk1Dealt())
                         .setTotalAtk2(battleFinalReport.getTotalAtk2() + damageReportRound.getTotalAtk2Dealt())
                         .setTakenAtk1(battleFinalReport.getTakenAtk1() + damageReportRound.getTotalAtk1Taken())
                         .setTakenAtk2(battleFinalReport.getTakenAtk2() + damageReportRound.getTotalAtk2Taken())
                         .setEscortShipLost(battleFinalReport.getEscortShipLost() + damageReportRound.getEscortShipLost())
                         .setMotherShipDied(battleFinalReport.getMotherShipDied() + damageReportRound.getMotherShipLost())
                         .setMotherShipHpLost(battleFinalReport.getMotherShipHpLost() + damageReportRound.getTotalHpMotherShipLost())
                         .setNpcLostHp(battleFinalReport.getNpcLostHp() + damageReportRound.getNpcLostHp());
        return battleFinalReport;
    }

    public void staticAttack(Attack attack) {

        // static escortShip
        Long escortShipLost = 0L;
        Long totalEscortShipJoined = 0L;
        for (EscortShipType escortShipType : EscortShipType.values()) {
            String key = builtKeyShip(ShipType.ESCORT_SHIP, escortShipType);
            List<BattleUnit> shipBattleList = attack.getBattleFields().getOrDefault(key, new ArrayList<>());
            for (BattleUnit shipBattle : shipBattleList) {
                escortShipLost += shipBattle.getKilled();
                totalEscortShipJoined += shipBattle.getFighting();
            }
        }
        attack.setTotalEscortShipKilled(escortShipLost);
        attack.setTotalEscortShipJoined(totalEscortShipJoined);

        // static: motherShipLost, hpMotherShipLost
        Long motherShipLost = 0L;
        Long totalHpMotherShipLost = 0L;
        Long totalMotherShipJoined = 0L;
        List<BattleUnit> motherShipBattleList = attack.getBattleFields().getOrDefault(builtKeyShip(ShipType.MOTHER_SHIP, null),
                                                                                      new ArrayList<>()); // filter isTookDamage
        for (BattleUnit shipBattle : motherShipBattleList) {
            MotherShipBattle motherShipBattle = (MotherShipBattle) shipBattle;
            motherShipLost += shipBattle.getKilled();
            totalHpMotherShipLost += motherShipBattle.getHpAfterBattle() - motherShipBattle.getCurrentHp();
            totalMotherShipJoined += motherShipBattle.getFighting();
        }
        attack.setTotalMotherShipLost(motherShipLost);
        attack.setTotalHpMotherShipLost(totalHpMotherShipLost);
        attack.setTotalMotherShipJoined(totalMotherShipJoined);

        // static Boss
        Long npcLostHp = 0L;
        List<BattleUnit> bossBattle = attack.getBattleFields().getOrDefault(builtKeyBattleUnit(BattleUnitType.BOSS, null), new ArrayList<>());
        for (BattleUnit battleUnit : bossBattle) {
            BossSeaBattle bossSeaBattle = (BossSeaBattle) battleUnit;
            npcLostHp += bossSeaBattle.getHpAfterBattle() - bossSeaBattle.getCurrentHp();
        }
        attack.setNpcLostHp(npcLostHp);

        // todo if need info
    }

    private void battlePveStatic(BattlePvEStatic battlePvEStatic, Attack attack) {
        battlePvEStatic.setTotalAtk1Dealt(battlePvEStatic.getTotalAtk1Dealt() + attack.getTotalAtk1Dealt());
        battlePvEStatic.setTotalAtk2Dealt(battlePvEStatic.getTotalAtk2Dealt() + attack.getTotalAtk2Dealt());
    }

    private FactionType checkFactionWinBattle(Attack attacker, Attack defender) {
        Boolean attackerLose = checkLostBattle(attacker.getBattleFields(), FactionType.ATTACKER);
        Boolean defenderLose = checkLostBattle(defender.getBattleFields(), FactionType.DEFENDER);
        if (attackerLose && defenderLose) {
            return FactionType.ATTACKER;
        } else if (attackerLose) {
            return FactionType.DEFENDER;
        } else if (defenderLose) {
            return FactionType.ATTACKER;
        }
        return null;
    }

    private Boolean checkLostBattle(Map<String, List<BattleUnit>> maps, FactionType factionType) {
        switch (factionType) {
            case ATTACKER:
                List<? extends BattleUnit> listMotherShip = maps.get(builtKeyShip(ShipType.MOTHER_SHIP, null));
                for (BattleUnit shipBattle : listMotherShip) {
                    if (shipBattle.getCurrentHp() > 0) {
                        return false;
                    }
                }
                return true;
            case DEFENDER:
                List<? extends BattleUnit> listBoss = maps.get(builtKeyBattleUnit(BattleUnitType.BOSS, null));
                for (BattleUnit battleUnit : listBoss) {
                    if (battleUnit.getCurrentHp() > 0) {
                        return false;
                    }
                }
                return true;
            default:
                throw KOSException.of(ErrorCode.BAD_REQUEST_ERROR);
        }
    }

    public void settleLineup(Attack attack) {
        Map<String, List<BattleUnit>> maps = attack.getBattleFields();
        for (Entry<String, List<BattleUnit>> entry : maps.entrySet()) {
            for (BattleUnit battleUnit : entry.getValue()) {
                battleUnit.setFighting(battleUnit.getAmount());
                if (battleUnit instanceof EscortShipBattle) {
                    EscortShipBattle escortShipBattle = (EscortShipBattle) battleUnit;
                    escortShipBattle.setCurrentHp((escortShipBattle.getFighting() * escortShipBattle.getHp() - escortShipBattle.getHpLost()));
                }
            }
        }
    }

    private void calculateAfterBattleForDefender(Attack attack, Attack victim) {
        victim.setTotalAtk1Taken(attack.getTotalAtk1Dealt());
        victim.setTotalAtk2Taken(attack.getTotalAtk2Dealt());
        Map<String, List<BattleUnit>> battleFieldsVictim = victim.getBattleFields();
        List<BattleUnit> battleUnits = battleFieldsVictim.get(builtKeyBattleUnit(BattleUnitType.BOSS, null));
        AttackDamage attackDamage = new AttackDamage().setAtk1(victim.getTotalAtk1Taken())
                                                      .setAtk2(victim.getTotalAtk2Taken());
        takeDamage(attackDamage, battleUnits);
    }

    private void validateBattleReport(List<BattleProfile> battleProfiles, BattleReport battleReport) {
        for (BattleProfile battleProfile : battleProfiles) {
            if (Objects.nonNull(battleProfile.getKosProfile())
                && userBattleReportRepository.findByBattleIdAndKosProfileId(battleReport.getBattle().getId(),
                                                                            battleProfile.getKosProfile().getId()).isEmpty()) {
                var userBattleReport = new UserBattleReport();
                userBattleReport.setBattleReport(battleReport);
                userBattleReport.setBattleProfile(battleProfile);
                userBattleReportRepository.save(userBattleReport);
            }
        }
    }

    private void calculateAfterBattle(Attack attack, Attack victim, BattleFieldLineUpConfig config) {
        victim.setTotalAtk1Taken(attack.getTotalAtk1Dealt());
        victim.setTotalAtk2Taken(attack.getTotalAtk2Dealt());
        Long totalAtk1Taken = victim.getTotalAtk1Taken();
        Long totalAtk2Taken = victim.getTotalAtk2Taken();
        Map<String, List<BattleUnit>> battleFieldsVictim = victim.getBattleFields();
        AttackDamage attackDamageReserveRows = new AttackDamage().setAtk1(0L).setAtk2(0L);
        for (BattleFiledRowsConfig rows : config.getRows()) {
            // 1 hàng trong battleField
            Double totalPercentDamageTakenRow = totalPercentDamageTakenInRows(rows);
            AttackDamage attackDamageReserveInRows = new AttackDamage().setAtk1(0L).setAtk2(0L);
            for (BattleFiledElementConfig elementConfig : rows.getElements()) {
                // 1 ô trong battleField
                Long atk1Taken = Math.round(totalAtk1Taken * elementConfig.getPercentDamageTaken()
                                            + attackDamageReserveRows.getAtk1() * elementConfig.getPercentDamageTaken()
                                              / totalPercentDamageTakenRow
                                            + attackDamageReserveInRows.getAtk1());
                Long atk2Taken = Math.round(totalAtk2Taken * elementConfig.getPercentDamageTaken()
                                            + attackDamageReserveRows.getAtk2() * elementConfig.getPercentDamageTaken()
                                              / totalPercentDamageTakenRow
                                            + attackDamageReserveInRows.getAtk2());
                List<BattleUnit> listShipBattleVictim = battleFieldsVictim.get(
                        builtKeyShip(elementConfig.getShipType(), elementConfig.getEscortShipType()));
                if (listShipBattleVictim == null) {
                    attackDamageReserveInRows.setAtk1(atk1Taken)
                                             .setAtk2(atk2Taken);
                } else {
                    AttackDamage attackDamage = new AttackDamage().setAtk1(atk1Taken)
                                                                  .setAtk2(atk2Taken);
                    attackDamageReserveInRows = takeDamage(attackDamage, listShipBattleVictim);
                }

            }
            attackDamageReserveRows.setAtk1(attackDamageReserveInRows.getAtk1())
                                   .setAtk2(attackDamageReserveInRows.getAtk2());
        }
    }

    private AttackDamage takeDamage(AttackDamage attackDamage, List<? extends BattleUnit> list) {
        for (BattleUnit battleUnit : list) {
            battleUnit.setHpLostAfterRound(0L);
            if (attackDamage.getAtk1() <= 0 && attackDamage.getAtk2() <= 0) {
                battleUnit.setAtk1Redundant(0L);
                battleUnit.setAtk2Redundant(0L);
                return attackDamage.setAtk1(0L).setAtk2(0L);
            }
            if (battleUnit.getFighting() <= 0 || battleUnit.getCurrentHp() <= 0) {
                battleUnit.setAtk1Redundant(attackDamage.getAtk1());
                battleUnit.setAtk2Redundant(attackDamage.getAtk2());
                continue;
            }
            takeDamageToShipBattle(attackDamage, battleUnit);
        }
        return attackDamage;
    }

    public AttackDamage takeDamageToShipBattle(AttackDamage attackDamage, BattleUnit shipBattle) {
        // for test
        shipBattle.setTotalAtk1Taken(attackDamage.getAtk1())
                  .setTotalAtk2Taken(attackDamage.getAtk2());
        // end for test

        Long currentHp = shipBattle.getCurrentHp();
        Long atk1_ = shipBattle.getDef1() >= 1 ? attackDamage.getAtk1() : Math.min(attackDamage.getAtk1(),
                                                                                   Math.round(currentHp / (1 - shipBattle.getDef1())));
        Long atk2_ = shipBattle.getDef2() >= 1 ? attackDamage.getAtk2() : Math.min(attackDamage.getAtk2(),
                                                                                   Math.round(currentHp / (1 - shipBattle.getDef2())));

        Long hp1 = Math.round(atk1_ * (1 - shipBattle.getDef1()));
        Long hp2 = Math.round(atk2_ * (1 - shipBattle.getDef2()));
        Long totalHp = hp1 + hp2;
        if (totalHp > currentHp) {
            shipBattle.setHpLostAfterRound(currentHp).setCurrentHp(0L).setIsTookDamage(true);
            Double ratio = totalHp.doubleValue() / currentHp;
            Long atk1Need = Math.round(atk1_ / ratio);
            Long atk2Need = Math.round(atk2_ / ratio);
            attackDamage.setAtk1(attackDamage.getAtk1() - atk1Need);
            attackDamage.setAtk2(attackDamage.getAtk2() - atk2Need);
        } else if (totalHp < currentHp) {
            shipBattle.setHpLostAfterRound(totalHp).setCurrentHp(shipBattle.getCurrentHp() - totalHp).setIsTookDamage(true);
            attackDamage.setAtk1(0L)
                        .setAtk2(0L);
        } else {
            attackDamage.setAtk1(attackDamage.getAtk1() - atk1_)
                        .setAtk2(attackDamage.getAtk2() - atk2_);
            shipBattle.setHpLostAfterRound(currentHp).setCurrentHp(0L).setIsTookDamage(true);
        }
        shipBattle.setAtk1Redundant(attackDamage.getAtk1())
                  .setAtk2Redundant(attackDamage.getAtk2());
        return attackDamage;
    }

    private Double totalPercentDamageTakenInRows(BattleFiledRowsConfig rowsConfig) {
        Double result = 0D;
        for (BattleFiledElementConfig config : rowsConfig.getElements()) {
            result += config.getPercentDamageTaken();
        }
        return RoundUtil.roundDouble5Decimal(result);
    }

    private Attack calculateTotalAtkAttack(Attack attack) {
        Long totalAtk1Dealt = 0L;
        Long totalAtk2Dealt = 0L;
        Set<String> setKeys = attack.getBattleFields().keySet();
        for (String s : setKeys) {
            List<? extends BattleUnit> shipBattles = attack.getBattleFields().get(s);
            for (BattleUnit battleUnit : shipBattles) {
                Integer fighting = (int) Math.ceil(battleUnit.getCurrentHp().doubleValue() / battleUnit.getHp());
                totalAtk1Dealt += battleUnit.getAtk1() * fighting;
                totalAtk2Dealt += battleUnit.getAtk2() * fighting;

                // for test
                battleUnit.setTotalAtk1Dealt(battleUnit.getAtk1() * fighting)
                          .setTotalAtk2Dealt(battleUnit.getAtk2() * fighting);

            }
        }
        attack.setTotalAtk1Dealt(totalAtk1Dealt);
        attack.setTotalAtk2Dealt(totalAtk2Dealt);

        return attack;
    }

    public BattleFieldLineUpConfig chooseBattleField(Attack attacker, Attack defender) {
        Integer totalShip = Integer.MAX_VALUE;
        BattleFieldConfig config = battleFieldConfigService.chooseBattleFieldByThresh(totalShip);
        BattleFieldLineUpConfig battleFieldLineupConfig = config.getBattleFieldLineupModelConfig();
        BattlefieldDamageConfig damageConfig = kosConfigService.getBattleFieldDamageConfig();
        Integer row = 0;
        for (List<BattleFieldElementDamageConfig> list : damageConfig.getBattleFieldDamageConfig()) {
            Integer col = 0;
            for (BattleFieldElementDamageConfig elementDamageConfig : list) {
                BattleFiledElementConfig elementConfig = battleFieldLineupConfig.getRows().get(row).getElements().get(col);
                elementConfig.setPercentDamageTaken(elementDamageConfig.getPercentDamageTaken());
                col++;
            }
            row++;
        }
        String battleFieldInfo = String.format("id: %s, minShip : %s, name : %s", config.getId(), config.getMinShip(), config.getName());
        attacker.setBattleFieldInfo(battleFieldInfo);
        defender.setBattleFieldInfo(battleFieldInfo);
        return battleFieldLineupConfig;
    }

    public BattleRound getCurrentRoundBattle(Battle battle) {
        BattleRound battleRound = battleRoundRepository.findByBattle_IdAndIndex(battle.getId(), battle.getCurrentRound()).orElseThrow(
                () -> KOSException.of(ErrorCode.BATTLE_ROUND_NOT_FOUND));
        return battleRound;
    }

    public void cancelBattle(Battle battle) {
        if (BattleStatus.INIT.equals(battle.getStatus())) {
            battle.setStatus(BattleStatus.END);
            battleRepository.save(battle);
        }
    }

    private void staticWarInfoAfterRound(BattleRoundSnapshot snapshot) {
        List<KosWarInfo> warInfoAttacker = getListWarInfo(snapshot.getAttackerModel());
        //attacker
        updateWarInfo(warInfoAttacker, snapshot.getDefenderModel());
        // defender: there is no defender because a is the boss
    }

    public void updateWarInfo(List<KosWarInfo> warInfo, Attack defender) {
        for (KosWarInfo kosWarInfo : warInfo) {
            kosWarInfo.setWarshipsDestroyed(
                    kosWarInfo.getWarshipsDestroyed() + defender.getTotalEscortShipKilled() + defender.getTotalMotherShipLost());
        }
        kosWarInfoService.saveAll(warInfo);
    }

    private List<KosWarInfo> getListWarInfo(Attack attack) {
        Set<Long> kosProfileIdAttacker = new HashSet<>();
        for (var entry : attack.getBelligerentJoined().entrySet()) {
            if (Objects.nonNull(entry.getValue().getKosProfileId())) {
                kosProfileIdAttacker.add(entry.getValue().getKosProfileId());
            }
        }
        return kosWarInfoService.findByKosProfile_IdIn(kosProfileIdAttacker);
    }

    private void updateWarInfoAfterBattle(Battle battle) {
        KosWarInfo attackerWarInfo = kosWarInfoService.getByKosProfileId(battle.getAttacker().getKosProfile().getId());
        if (battle.getWinner().getId().equals(battle.getAttacker().getId())) {
            attackerWarInfo.setWin(attackerWarInfo.getWin() + 1);
            attackerWarInfo.setBossKilled(attackerWarInfo.getBossKilled() + 1);
        } else {
            attackerWarInfo.setLose(attackerWarInfo.getLose() + 1);
        }
    }
}
