package com.supergroup.kos.building.domain.service.battle;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.supergroup.kos.building.domain.async.BattleReportServiceAsyncTask;
import com.supergroup.kos.building.domain.async.MotherShipServiceAsyncTask;
import com.supergroup.kos.building.domain.async.SeaActivityAsyncTask;
import com.supergroup.kos.building.domain.command.PrepareShipLineupCommand;
import com.supergroup.kos.building.domain.command.SaveOrUpdateElementCommand;
import com.supergroup.kos.building.domain.command.WithdrawActivityCommand;
import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.building.domain.constant.battle.BattleCancelReason;
import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.constant.battle.BattleType;
import com.supergroup.kos.building.domain.constant.battle.FactionType;
import com.supergroup.kos.building.domain.constant.battle.FlatCheckResultBattle;
import com.supergroup.kos.building.domain.constant.battle.ShipType;
import com.supergroup.kos.building.domain.constant.item.ItemId;
import com.supergroup.kos.building.domain.constant.seamap.SeaActivityStatus;
import com.supergroup.kos.building.domain.dto.battle.CreateTaskBattleEvent;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.battle.BattleFinalReport;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;
import com.supergroup.kos.building.domain.model.battle.BattleReport;
import com.supergroup.kos.building.domain.model.battle.BattleRound;
import com.supergroup.kos.building.domain.model.battle.BattleRoundSnapshot;
import com.supergroup.kos.building.domain.model.battle.BattleUnit;
import com.supergroup.kos.building.domain.model.battle.CheckResultBattle;
import com.supergroup.kos.building.domain.model.battle.CheckWinBattleResult;
import com.supergroup.kos.building.domain.model.battle.DamageReport;
import com.supergroup.kos.building.domain.model.battle.EscortShipBattle;
import com.supergroup.kos.building.domain.model.battle.EscortShipReport;
import com.supergroup.kos.building.domain.model.battle.MotherShipBattle;
import com.supergroup.kos.building.domain.model.battle.MotherShipReport;
import com.supergroup.kos.building.domain.model.battle.NextRoundWithdrawal;
import com.supergroup.kos.building.domain.model.battle.ReverseEscortShipReport;
import com.supergroup.kos.building.domain.model.battle.ReverseMotherShipReport;
import com.supergroup.kos.building.domain.model.battle.RoundReport;
import com.supergroup.kos.building.domain.model.battle.RoundUsedItem;
import com.supergroup.kos.building.domain.model.battle.ShipLineUpBattle;
import com.supergroup.kos.building.domain.model.battle.UserBattleReport;
import com.supergroup.kos.building.domain.model.battle.logic.Attack;
import com.supergroup.kos.building.domain.model.battle.logic.AttackDamage;
import com.supergroup.kos.building.domain.model.battle.logic.BattleProgress;
import com.supergroup.kos.building.domain.model.battle.logic.Belligerent;
import com.supergroup.kos.building.domain.model.config.EscortShipConfig;
import com.supergroup.kos.building.domain.model.config.battle.BattleFieldConfig;
import com.supergroup.kos.building.domain.model.config.battle.BattleFieldElementDamageConfig;
import com.supergroup.kos.building.domain.model.config.battle.BattleFieldLineUpConfig;
import com.supergroup.kos.building.domain.model.config.battle.BattleFiledElementConfig;
import com.supergroup.kos.building.domain.model.config.battle.BattleFiledRowsConfig;
import com.supergroup.kos.building.domain.model.config.battle.BattleTimeConfigDetail;
import com.supergroup.kos.building.domain.model.config.battle.BattlefieldDamageConfig;
import com.supergroup.kos.building.domain.model.config.battle.DefBattleConfig;
import com.supergroup.kos.building.domain.model.item.Item;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.EscortShipSquad;
import com.supergroup.kos.building.domain.model.seamap.KosWarInfo;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.Ship;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.model.ship.EscortShip;
import com.supergroup.kos.building.domain.model.ship.MotherShip;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleProfileRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRoundRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRoundSnapshotRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.EscortShipReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.MotherShipReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.ReverseEscortShipReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.ReverseMotherShipReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.RoundReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.RoundUseItemRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.UserBattleReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.item.ItemRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.NextRoundWithdrawalRepo;
import com.supergroup.kos.building.domain.service.config.KosConfigService;
import com.supergroup.kos.building.domain.service.seamap.EscortShipSquadService;
import com.supergroup.kos.building.domain.service.seamap.KosWarInfoService;
import com.supergroup.kos.building.domain.service.seamap.MapService;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;
import com.supergroup.kos.building.domain.service.seamap.activity.LineUpService;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;
import com.supergroup.kos.building.domain.service.ship.EscortShipConfigService;
import com.supergroup.kos.building.domain.service.ship.EscortShipService;
import com.supergroup.kos.building.domain.service.ship.MotherShipService;
import com.supergroup.kos.building.domain.task.BattleTask;

import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class BattleLiberateService implements BattleHandler {

    public static final String                            BATTLE_EXCHANGE = "BATTLE_EXCHANGE";
    public static final String                            BATTLE_QUEUE    = "BATTLE_QUEUE";
    @Delegate
    private final       BattleRepository                  battleRepository;
    private final       LineUpService                     lineUpService;
    private final       BattleRoundRepository             battleRoundRepository;
    private final       MotherShipService                 motherShipService;
    private final       EscortShipService                 escortShipService;
    private final       EscortShipSquadService            escortShipSquadService;
    private final       BattleFieldConfigService          battleFieldConfigService;
    private final       KosConfigService                  kosConfigService;
    private final       BattleRoundSnapshotRepository     battleRoundSnapshotRepository;
    private final       BattleUnitService                 shipBattleService;
    private final       BattleProfileRepository           battleProfileRepository;
    private final       BattleReportRepository            battleReportRepository;
    private final       RoundReportRepository             roundReportRepository;
    private final       MotherShipReportRepository        motherShipReportRepository;
    private final       EscortShipReportRepository        escortShipReportRepository;
    private final       ReverseMotherShipReportRepository reverseMotherShipReportRepository;
    private final       ReverseEscortShipReportRepository reverseEscortShipReportRepository;
    private final       RoundUseItemRepository            roundUseItemRepository;
    private final       RabbitTemplate                    rabbitTemplate;
    private final       SeaActivityService                seaActivityService;
    private final       BattleProfileService              battleProfileService;
    private final       BattleReportService               battleReportService;
    private final MapService                   mapService;
    private final EscortShipConfigService      escortShipConfigService;
    private final BattleRoundSnapShotService   battleRoundSnapShotService;
    private final NextRoundWithdrawalRepo      nextRoundWithdrawalRepo;
    private final UserBattleReportRepository   userBattleReportRepository;
    private final ApplicationEventPublisher    publisher;
    private final BattleReportServiceAsyncTask battleReportServiceAsyncTask;
    private final KosWarInfoService            kosWarInfoService;
    private final InitBattleService            initBattleService;
    private final SeaActivityAsyncTask         seaActivityAsyncTask;
    private final UserBaseService              userBaseService;
    private final MotherShipServiceAsyncTask   motherShipServiceAsyncTask;

    public Battle save(Battle battle) {
        return battleRepository.save(battle);
    }

    @Transactional
    public Battle startBattle(Battle battle, SeaActivity attacker, SeaElement battleField) {
        log.info("Battle[{}]: Start battle", battle.getId());
        LocalDateTime now = LocalDateTime.now();
        // set up time battle start
        battle.getBattleReport().setStartAt(now);
        battleReportRepository.save(battle.getBattleReport());
        // set up battle field
        battleField.setBattle(battle);
        battle.setBattleField(battleField);
        // update shipLineUp for battle
        createShipLineUpWhenBattleForBase(battle.getAttacker(), now);
        updateShipLineUpForDefender(battle, now);

        beforeBattleProgress(battle);
        battleRepository.save(battle);
        mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(battleField));
        BattleTimeConfigDetail config = getBattleTimeConfigDetail();
        long roundDurationInMillis = config.getProgressDuration() * 1000;
        updateAllBattleActivityTimer(battle, LocalDateTime.now().plus(roundDurationInMillis, ChronoUnit.MILLIS));
        log.info("Start battle {} end", battle.getId());
        return battle;
    }

    public Battle validateBeforeStartBattle(Battle battle) {
        List<BattleStatus> status = List.of(BattleStatus.INIT, BattleStatus.PROGRESS, BattleStatus.BREAK);
        if (battleRepository.existsByAttackerKosProfileIdAndBattleTypeAndStatusInAndIdNot(battle.getAttacker().getKosProfile().getId(),
                                                                                          BattleType.LIBERATE, status, battle.getId())) {
            battle.setStatus(BattleStatus.CANCEL).setCancelReason(BattleCancelReason.EXISTS);
            return battleRepository.save(battle);
        }
        return battle;
    }

    @Transactional
    public void liberate(KosProfile kosProfile) {
        UserBase userBase = kosProfile.getBase();
        if (Objects.isNull(userBase) || !userBase.isOccupied()) {
            return;
        }
        userBase.setInitLiberateAt(LocalDateTime.now());
//        Battle battle = initBattleService.initBattle(BattleType.LIBERATE, userBase, userBase.getInvader().getKosProfileInvader().getBase(),
//                                                     userBase.getCoordinates());

        // don't save battle
        Battle battle = new Battle().setId(null).setBattleType(BattleType.LIBERATE).setStatus(BattleStatus.INIT).setBattleField(userBase);
        seaActivityAsyncTask.sendAttackNotification(userBase.getInvader().getKosProfileInvader().getUser().getId(), userBase.getCoordinates());
        mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(userBase));
        publisher.publishEvent(new CreateTaskBattleEvent(battle));
    }

    private void updateShipLineUpForDefender(Battle battle, LocalDateTime now) {
        List<BattleProfile> battleProfiles = battle.getBattleProfiles().stream().filter(b -> FactionType.DEFENDER.equals(b.getFaction())).collect(
                Collectors.toList());
        Map<Long, BattleProfile> battleProfileMap = createMapBattleProfile(battleProfiles);// key = kosProfileId;
        List<ShipLineUp> shipLineUps = seaActivityService.getListOccupyInElement(battle.getBattleField().getId()).stream().map(SeaActivity::getLineUp)
                                                         .collect(
                                                                 Collectors.toList());
        shipLineUps = sortLineUpWhenStartBattle(shipLineUps);
        Long count = 0L;
        for (ShipLineUp shipLineUp : shipLineUps) {
            BattleProfile battleProfile = battleProfileMap.get(shipLineUp.getActivity().getKosProfile().getId());
            if (Objects.isNull(battleProfile)) {
                battleProfile = battleProfileService.save(battleProfileService.createUserBattleProfile(
                        shipLineUp.getActivity().getKosProfile(),
                        battle, FactionType.DEFENDER));
            }
            battleProfileMap.put(battleProfile.getKosProfile().getId(), battleProfile);
            shipLineUp.setTimeJoinedBattle(now.plus(count, ChronoUnit.MILLIS)).setBattleProfile(battleProfile);
            count++;
        }
        shipLineUps = lineUpService.saveAll(shipLineUps);

        // update battleProfile
        for (ShipLineUp shipLineUp : shipLineUps) {
            List<ShipLineUp> shipLineUpInBattleProfiles = shipLineUp.getBattleProfile().getShipLineUps();
            if (Objects.isNull(shipLineUpInBattleProfiles)) {
                shipLineUpInBattleProfiles = new ArrayList<>();
            }
            shipLineUpInBattleProfiles.add(shipLineUp);
            shipLineUp.getBattleProfile().setShipLineUps(shipLineUpInBattleProfiles);
        }
    }

    public Map<Long, BattleProfile> createMapBattleProfile(List<BattleProfile> battleProfiles) {
        Map<Long, BattleProfile> result = new HashMap<>(); // key = kosProfile
        for (BattleProfile battleProfile : battleProfiles) {
            result.put(battleProfile.getKosProfile().getId(), battleProfile);
        }
        return result;
    }

    private List<ShipLineUp> sortLineUpWhenStartBattle(List<ShipLineUp> shipLineUps) {
        shipLineUps = shipLineUps.stream().sorted(Comparator.comparing(s -> s.getMotherShip().getCurrentHp())).collect(Collectors.toList());
        Collections.reverse(shipLineUps);
        return shipLineUps;
    }

    @Transactional
    public BattleProfile createShipLineUpWhenBattleForBase(BattleProfile battleProfile, LocalDateTime now) {
        Long kosProfileId = battleProfile.getKosProfile().getId();
        log.info("Battle : {} , Create ship line up for defender {}", battleProfile.getBattle().getId(), kosProfileId);
        List<MotherShip> motherShipList = motherShipService.findByKosProfileId(kosProfileId);
        List<EscortShip> escortShipList = escortShipService.getEscortShips(kosProfileId);
        List<ShipLineUp> shipLineUps = new ArrayList<>();

        // ship LineUp only EscortShip
        ShipLineUp shipLineUp = new ShipLineUp();
        shipLineUp.setTimeJoinedBattle(now).setBattleProfile(battleProfile);
        shipLineUp = lineUpService.save(shipLineUp);
        List<EscortShipSquad> escortShipSquadList = new ArrayList<>();
        for (EscortShip escortShip : escortShipList) {
            EscortShipSquad escortShipSquad = new EscortShipSquad().setEscortShip(escortShip)
                                                                   .setAmount(escortShip.getAmount())
                                                                   .setLineUp(shipLineUp);
            escortShip.setAmount(0L);
            escortShipSquadList.add(escortShipSquad);
        }
        escortShipSquadList = escortShipSquadService.saveAll(escortShipSquadList);
        shipLineUp.setEscortShipSquad(escortShipSquadList);
        shipLineUps.add(shipLineUp);

        motherShipList = motherShipList.stream()
                                       .filter(motherShip -> motherShip.getStatus().equals(SeaActivityStatus.STANDBY))
                                       .sorted(Comparator.comparing(MotherShip::getCurrentHp).reversed()).collect(Collectors.toList());
        // shipLineup only MotherShip
        for (MotherShip motherShip : motherShipList) {
            LocalDateTime joinTimestamp = LocalDateTime.now();
            ShipLineUp shipLineUp1 = lineUpService.updateLineUp(new PrepareShipLineupCommand().setEscortShips(new ArrayList<>())
                                                                                              .setKosProfileId(kosProfileId)
                                                                                              .setMotherShipId(motherShip.getId()))
                                                  .setTimeJoinedBattle(joinTimestamp);
            shipLineUp1.setMotherShip(motherShip)
                       .setBattleProfile(battleProfile);
            shipLineUps.add(shipLineUp1);
            motherShip.setStatus(SeaActivityStatus.ATTACKING);
        }
        shipLineUps = lineUpService.saveAll(shipLineUps);
        battleProfile.setShipLineUps(shipLineUps);
        return battleProfileRepository.save(battleProfile);
    }

    private void beforeBattleProgress(Battle battle) {
        Long currentRound = battle.getCurrentRound();
        BattleRound battleRound = new BattleRound().setIndex(currentRound + 1).setBattle(battle);
        battle.setCurrentRound(currentRound + 1).setStatus(BattleStatus.PROGRESS);
        // save to db
        battleRound = battleRoundRepository.save(battleRound);
        ShipLineUpBattle shipLineUpBattle = getListShipLineUpInBattle(battle);
        BattleRoundSnapshot snapshot = createBattleSnapshot(shipLineUpBattle, battleRound);
        updateFlatCheckResultBattle(battle, snapshot);
        battle = battleRepository.save(battle);

        CheckWinBattleResult checkWinBattleResult = checkFactionWinBattle(battle);
        log.info("Battle[{}]: Check win battle result {}", battle.getId(), battle.getStatus());
        if (checkWinBattleResult.getIsEnd()) {
            log.info("Battle[{}]: Process to end this round", battle.getId());
            finalRoundBattle(battle);
        } else {
            log.info("Battle[{}]: Send new round to queue", battle.getId());
            publisher.publishEvent(new CreateTaskBattleEvent(battle));
        }

    }

    private Battle updateFlatCheckResultBattle(Battle battle, BattleRoundSnapshot snapshot) {
        CheckResultBattle checkResultBattle = battle.getCheckResult();
        checkResultBattle.setAttackerCheckResult(
                                 getFlatCheckResultBattle(snapshot.getAttackerModel().getBattleFields(), checkResultBattle.getAttackerCheckResult()))
                         .setDefenderCheckResult(
                                 getFlatCheckResultBattle(snapshot.getDefenderModel().getBattleFields(), checkResultBattle.getDefenderCheckResult()));
        return battle;
    }

    private FlatCheckResultBattle getFlatCheckResultBattle(Map<String, List<BattleUnit>> battleFields, FlatCheckResultBattle currentFlat) {
        if (FlatCheckResultBattle.MOTHER_SHIP.equals(currentFlat)) {
            return currentFlat;
        }
        String key = builtKeyShip(ShipType.MOTHER_SHIP, null);
        if (battleFields.containsKey(key) && !battleFields.get(key).isEmpty()) {
            return FlatCheckResultBattle.MOTHER_SHIP;
        }
        return currentFlat;
    }

    private BattleRoundSnapshot createBattleSnapshot(ShipLineUpBattle shipLineUpBattle, BattleRound battleRound) {
        // todo map Ship
        List<ShipLineUp> shipLineUpAttacker = shipLineUpBattle.getShipLineupAttacker();
        List<ShipLineUp> shipLineUpDefender = shipLineUpBattle.getShipLineupDefender();

        // to mapShip and filter ship
        Map<String, List<Ship>> mapShipAttacker = toMapShip(shipLineUpAttacker);
        Map<String, List<Ship>> mapShipDefender = toMapShip(shipLineUpDefender);

        // create attacker, defender
        Attack attacker = new Attack();
        Attack defender = new Attack();

        // create belligerent joined
        createBelligerentJoined(attacker, shipLineUpAttacker);
        createBelligerentJoined(defender, shipLineUpDefender);
        // items use
        checkItemUse(attacker);
        checkItemUse(defender);

        // built battlefield for attack
        createBattleFieldForAttack(attacker, mapShipAttacker);
        createBattleFieldForAttack(defender, mapShipDefender);

        // create snapshot
        BattleRoundSnapshot snapshot = new BattleRoundSnapshot().setBattleRound(battleRound)
                                                                .setAttackerModel(attacker)
                                                                .setDefenderModel(defender);
        battleRound.setBattleRoundSnapshot(snapshot);

        // save to db
//        battleRoundRepository.save(battleRound);// check todo
        return battleRoundSnapshotRepository.save(snapshot);
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

    private void createBelligerent(Attack attack) {
        Map<Long, Belligerent> belligerentJoined = new HashMap<>();
        Map<String, List<BattleUnit>> battleFields = attack.getBattleFields();
        for (Map.Entry<String, List<BattleUnit>> entry : battleFields.entrySet()) {
            for (BattleUnit shipBattle : entry.getValue()) {
                // filter if need todo
                Long battleProfileId = shipBattle.getBattleProfileId();
                if (!belligerentJoined.containsKey(battleProfileId)) {
                    Belligerent belligerent = new Belligerent().setBattleProfileId(battleProfileId);
                    belligerentJoined.put(belligerent.getBattleProfileId(), belligerent);
                }
            }
        }
        attack.setBelligerentJoined(belligerentJoined);
    }

    public void finalRoundBattle(Battle battle) {
        log.info("Battle[{}]: Final round. Current round {}", battle.getId(), battle.getCurrentRound());
        BattleRound round = getCurrentRoundBattle(battle);
        BattleRoundSnapshot snapshot = round.getBattleRoundSnapshot();

        Attack attacker = snapshot.getAttackerModel();
        Attack defender = snapshot.getDefenderModel();

        // valid battle report
        validateBattleReport(battle.getBattleProfiles(), battle.getBattleReport());

        // battle progress
        BattleProgress battleProgress = new BattleProgress().setAttacker(attacker).setDefender(defender);
        battleProgress(battleProgress);

        // update to Database
        updateShipToDatabase(attacker.getBattleFields());
        updateShipToDatabase(defender.getBattleFields());

        // create battleRoundReport
        RoundReport roundReport = new RoundReport().setRound(round)
                                                   .setBattleReport(round.getBattle().getBattleReport());
        roundReport = roundReportRepository.save(roundReport);
        updateRoundReport(roundReport, attacker, defender);
        round.setReport(roundReport);
        round = battleRoundRepository.save(round);
        snapshot = battleRoundSnapshotRepository.save(snapshot.setCurrentRound(battle.getCurrentRound()));

        // update WarInfo after Round
        staticWarInfoAfterRound(snapshot);
        log.info("Battle[{}]: Round - {}. Snapshot - {}", battle.getId(), round.getId(), snapshot.getId());
        battleReportService.sendRoundReport(roundReport);
        // check end battle
        CheckWinBattleResult checkWinBattleResult = checkFactionWinBattle(battle);
        if (checkWinBattleResult.getIsEnd()) {
            onBattleEnded(battle, checkWinBattleResult.getFactionTypeWin());
            return;
        }
        battle.setStatus(BattleStatus.BREAK);
        battleRepository.save(battle);
    }

    private void validateBattleReport(List<BattleProfile> battleProfiles, BattleReport battleReport) {
        for (BattleProfile battleProfile : battleProfiles) {
            if (!userBattleReportRepository.existByBattleProfileIdAndBattleReportId(battleProfile.getId(), battleReport.getId())) {
                var userBattleReport = new UserBattleReport();
                userBattleReport.setBattleReport(battleReport);
                userBattleReport.setBattleProfile(battleProfile);
                userBattleReportRepository.save(userBattleReport);
            }
        }
    }

    private void updateAllBattleActivityTimer(Battle battle, LocalDateTime timeEnd) {
        BattleStatus battleStatus = battle.getStatus();
        if (battleStatus.equals(BattleStatus.END) || battleStatus.equals(BattleStatus.CANCEL)) {return;}
        for (BattleProfile battleProfile : battle.getBattleProfiles()) {
            SeaActivityStatus status;
            if (battleStatus.equals(BattleStatus.BREAK)) {
                status = SeaActivityStatus.BREAK;
            } else {
                if (battleProfile.getFaction().equals(FactionType.DEFENDER)) {
                    status = SeaActivityStatus.DEFENDING;
                } else {
                    status = SeaActivityStatus.ATTACKING;
                }
            }
            for (ShipLineUp lineUp : battleProfile.getShipLineUps()) {
                SeaActivity activity = lineUp.getActivity();
                if (Objects.nonNull(activity)) {
                    seaActivityService.updateCurrentActionStatus(activity, timeEnd, status);
                    continue;
                }
                if (Objects.nonNull(lineUp.getMotherShip())) {
                    lineUp.getMotherShip().setStatus(status);
                    lineUpService.save(lineUp);

                    motherShipServiceAsyncTask.sendHealingNotification(lineUp.getMotherShip());
                }
            }
        }
    }

    @Transactional
    public void changeStatusBattle(BattleTask battleTask) {
        // create and start battle if battle isn't create
        // else get battle from database and start
        if (Objects.isNull(battleTask.getBattleId())) {
            log.info("Battle is not created! Create and start");
            UserBase userBase = userBaseService.getById(battleTask.getBattleFieldId());

            //valid after init and start battle
            if (Objects.isNull(userBase.getInitLiberateAt()) ||
                !userBase.isOccupied() ||
                Objects.nonNull(userBase.getBattle())) {
                return;
            }

            Battle battle = initBattleService.initBattle(BattleType.LIBERATE, userBase, userBase.getInvader().getKosProfileInvader().getBase(),
                                                         userBase.getCoordinates());
            Battle savedBattle = battleRepository.findById(battle.getId()).orElseThrow();
            startBattle(savedBattle, null, userBase);
        } else {
            Long battleId = battleTask.getBattleId();
            BattleTimeConfigDetail config = getBattleTimeConfigDetail();
            Battle battle = battleRepository.findById(battleId).orElseThrow(() -> KOSException.of(ErrorCode.BAD_REQUEST_ERROR));
            log.info("Battle[{}]: Processing... Status : {} - Current round : {}", battleId, battle.getStatus(), battle.getCurrentRound());
            // if battle is end or cancel ignore
            if (battle.getStatus().equals(BattleStatus.END) || battle.getStatus().equals(BattleStatus.CANCEL)) {
                log.info("Battle[{}]: Battle is {}. Ignore!", battleId, battle.getStatus());
                return;
            }
            long breakDurationInMillis = config.getBreakDuration() * 1000;
            long roundDurationInMillis = config.getProgressDuration() * 1000;

            // battle process
            switch (battle.getStatus()) {
                case PROGRESS:
                    finalRoundBattle(battle);
                    if (battle.getStatus().equals(BattleStatus.END) || battle.getStatus().equals(BattleStatus.CANCEL)) {
                        log.info("Battle[{}]: After round. Battle is {}. Stop process", battle.getId(), battle.getStatus());
                        return;
                    }
                    withdrawalLineUpAfterRound(battle);
                    CheckWinBattleResult checkWinBattleResult = checkFactionWinBattle(battle);
                    log.info("Battle[{}]: Check win battle result {}", battle.getId(), battle.getStatus());
                    if (checkWinBattleResult.getIsEnd()) {
                        log.info("Battle[{}]: Process to end this battle", battle.getId());
                        onBattleEnded(battle, checkWinBattleResult.getFactionTypeWin());
                    } else {
                        log.info("Battle[{}]: Send new round to queue", battle.getId());
                        publisher.publishEvent(new CreateTaskBattleEvent(battle));
                    }
                    updateAllBattleActivityTimer(battle, LocalDateTime.now().plus(breakDurationInMillis, ChronoUnit.MILLIS));
                    break;
                case BREAK:
                    beforeBattleProgress(battle);
                    updateAllBattleActivityTimer(battle, LocalDateTime.now().plus(roundDurationInMillis, ChronoUnit.MILLIS));
                    break;
                default:
                    break;
            }
        }
    }

    private Battle withdrawalLineUpAfterRound(Battle battle) {
        List<NextRoundWithdrawal> nextRoundWithdrawals = nextRoundWithdrawalRepo.findByBattleId(battle.getId());
        List<Long> idsLineupWithdrawal = nextRoundWithdrawals.stream().map(NextRoundWithdrawal::getLineUpId).distinct().collect(Collectors.toList());
        if (!idsLineupWithdrawal.isEmpty()) {
            List<ShipLineUp> shipLineUps = lineUpService.getLineupByIdIn(idsLineupWithdrawal)
                                                        .stream().filter(lineUp -> Objects.nonNull(lineUp.getActiveMotherShip()))
                                                        .collect(Collectors.toList());
            if (!shipLineUps.isEmpty()) {
                withdrawListMotherShips(shipLineUps.stream()
                                                   .map(ShipLineUp::getMotherShip)
                                                   .collect(Collectors.toList()));
            }
        }
        nextRoundWithdrawalRepo.deleteByBattleId(battle.getId());
        return battleRepository.save(battle);
    }

    @Transactional
    public BattleRound getCurrentRoundBattle(Battle battle) {
        BattleRound battleRound = battleRoundRepository.findByBattle_IdAndIndex(battle.getId(), battle.getCurrentRound()).orElseThrow(
                () -> KOSException.of(ErrorCode.BATTLE_ROUND_NOT_FOUND));
        return battleRound;
    }

    @Transactional
    public void battleProgress(BattleProgress battleProgress) {
        Attack attacker = battleProgress.getAttacker();
        Attack defender = battleProgress.getDefender();
        log.info("Battle progress {} {}", attacker.toString(), defender.toString());
        BattleFieldLineUpConfig battleFieldLineUpConfig = chooseBattleField(attacker, defender);

//        //createBelligerent
//        createBelligerent(attacker);
//        createBelligerent(defender);

        // calculate ship : fighting, reserve
        settleLineup(attacker, battleFieldLineUpConfig);
        settleLineup(defender, battleFieldLineUpConfig);

        // calculate total atk
        calculateTotalAtkAttack(attacker);
        calculateTotalAtkAttack(defender);

        // taken dame
        calculateAfterBattle(attacker, defender, battleFieldLineUpConfig);
        calculateAfterBattle(defender, attacker, battleFieldLineUpConfig);

        // static data
        staticAttack(attacker);
        staticAttack(defender);
    }

    private void staticAttack(Attack attack) {

        // static escortShip
        Long escortShipLost = 0L;
        Long totalEscortShipJoined = 0L;
        for (EscortShipType escortShipType : EscortShipType.values()) {
            String key = builtKeyShip(ShipType.ESCORT_SHIP, escortShipType);
            List<BattleUnit> shipBattleList = attack.getBattleFields().get(key);
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
        List<BattleUnit> motherShipBattleList = attack.getBattleFields().get(builtKeyShip(ShipType.MOTHER_SHIP, null)); // filter isTookDamage
        for (BattleUnit shipBattle : motherShipBattleList) {
            MotherShipBattle motherShipBattle = (MotherShipBattle) shipBattle;
            motherShipLost += shipBattle.getKilled();
            totalHpMotherShipLost += motherShipBattle.getHpAfterBattle() - motherShipBattle.getCurrentHp();
            totalMotherShipJoined += motherShipBattle.getFighting();
        }
        attack.setTotalMotherShipLost(motherShipLost);
        attack.setTotalHpMotherShipLost(totalHpMotherShipLost);
        attack.setTotalMotherShipJoined(totalMotherShipJoined);

        // todo if need info
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

    private Double totalPercentDamageTakenInRows(BattleFiledRowsConfig rowsConfig) {
        Double result = 0D;
        for (BattleFiledElementConfig config : rowsConfig.getElements()) {
            result += config.getPercentDamageTaken();
        }
        return RoundUtil.roundDouble5Decimal(result);
    }

    private AttackDamage takeDamage(AttackDamage attackDamage, List<? extends BattleUnit> list) {
        for (BattleUnit shipBattle : list) {
            if (attackDamage.getAtk1() <= 0 && attackDamage.getAtk2() <= 0) {
                return attackDamage.setAtk1(0L).setAtk2(0L);
            }
            if (shipBattle.getFighting() <= 0) {
                continue;
            }
            takeDamageToShipBattle(attackDamage, shipBattle);
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
            shipBattle.setCurrentHp(0L).setIsTookDamage(true);
            Double ratio = totalHp.doubleValue() / currentHp;
            Long atk1Need = Math.round(atk1_ / ratio);
            Long atk2Need = Math.round(atk2_ / ratio);
            attackDamage.setAtk1(attackDamage.getAtk1() - atk1Need);
            attackDamage.setAtk2(attackDamage.getAtk2() - atk2Need);
        } else if (totalHp < currentHp) {
            shipBattle.setCurrentHp(shipBattle.getCurrentHp() - totalHp).setIsTookDamage(true);
            attackDamage.setAtk1(0L)
                        .setAtk2(0L);
        } else {
            attackDamage.setAtk1(attackDamage.getAtk1() - atk1_)
                        .setAtk2(attackDamage.getAtk2() - atk2_);
            shipBattle.setCurrentHp(0L).setIsTookDamage(true);
        }
        shipBattle.setAtk1Redundant(attackDamage.getAtk1())
                  .setAtk2Redundant(attackDamage.getAtk2());
        return attackDamage;
    }

    private Attack calculateTotalAtkAttack(Attack attack) {
        Long totalAtk1Dealt = 0L;
        Long totalAtk2Dealt = 0L;
        Set<String> setKeys = attack.getBattleFields().keySet();
        for (String s : setKeys) {
            List<? extends BattleUnit> shipBattles = attack.getBattleFields().get(s);
            for (BattleUnit shipBattle : shipBattles) {
                totalAtk1Dealt += shipBattle.getAtk1() * shipBattle.getFighting();
                totalAtk2Dealt += shipBattle.getAtk2() * shipBattle.getFighting();

                // for test
                shipBattle.setTotalAtk1Dealt(shipBattle.getAtk1() * shipBattle.getFighting())
                          .setTotalAtk2Dealt(shipBattle.getAtk2() * shipBattle.getFighting());

            }
        }
        attack.setTotalAtk1Dealt(totalAtk1Dealt);
        attack.setTotalAtk2Dealt(totalAtk2Dealt);

        return attack;
    }

    public void settleLineup(Attack attack, BattleFieldLineUpConfig battleFieldLineUpConfig) {
        Map<String, List<BattleUnit>> mapShipBattles = attack.getBattleFields();
        Map<String, BattleFiledElementConfig> mapConfig = toMapBattleFieldElementsConfig(battleFieldLineUpConfig);

        Set<String> keys = mapConfig.keySet();
        for (String s : keys) {
            List<BattleUnit> list = mapShipBattles.get(s);
            BattleFiledElementConfig config = mapConfig.get(s);
            Integer quantity = config.getQuantity();
            switch (config.getShipType()) {
                case MOTHER_SHIP:
                    for (BattleUnit shipBattle : list) {
                        MotherShipBattle motherShipBattle = (MotherShipBattle) shipBattle;
                        if (quantity > 0) {
                            motherShipBattle.setFighting(1);
                            quantity -= 1;
                        } else {
                            motherShipBattle.setFighting(0);
                        }
                    }
                    break;
                case ESCORT_SHIP:
                    for (BattleUnit shipBattle : list) {
                        EscortShipBattle escortShipBattle = (EscortShipBattle) shipBattle;
                        if (quantity <= 0) {
                            escortShipBattle.setCurrentHp(0L);
                            continue;
                        }
                        if (quantity >= escortShipBattle.getAmount()) {
                            escortShipBattle.setFighting(escortShipBattle.getAmount());
                            quantity -= escortShipBattle.getAmount();
                        } else {
                            escortShipBattle.setFighting(quantity);
                            quantity = 0;

                        }
                        escortShipBattle.setCurrentHp((escortShipBattle.getFighting() * escortShipBattle.getHp() - escortShipBattle.getHpLost()));
                        var a = 1L;
                    }
                    break;
                default:
                    break;
            }

        }
    }

    public BattleFieldLineUpConfig chooseBattleField(Attack attacker, Attack defender) {
        Integer totalShip = 0;
        Map<String, List<BattleUnit>> shipAttacker = attacker.getBattleFields();
        Map<String, List<BattleUnit>> shipDefender = defender.getBattleFields();
        for (Map.Entry<String, List<BattleUnit>> entry : shipAttacker.entrySet()) {
            if (!builtKeyShip(ShipType.MOTHER_SHIP, null).equals(entry.getKey())) {
                List<BattleUnit> list = entry.getValue();
                for (BattleUnit shipBattle : list) {
                    totalShip += shipBattle.getAmount();
                }
            }
        }
        attacker.setTotalShip(totalShip);
        for (Map.Entry<String, List<BattleUnit>> entry : shipDefender.entrySet()) {
            if (!builtKeyShip(ShipType.MOTHER_SHIP, null).equals(entry.getKey())) {
                List<BattleUnit> list = entry.getValue();
                for (BattleUnit shipBattle : list) {
                    totalShip += shipBattle.getAmount();
                }
            }
        }
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
        defender.setTotalShip(totalShip - attacker.getTotalShip())
                .setBattleFieldInfo(battleFieldInfo);
        return battleFieldLineupConfig;
    }

    public String builtKeyShip(ShipType shipType, EscortShipType escortShipType) {
        if (shipType.equals(ShipType.MOTHER_SHIP)) {
            return shipType.name().toString();
        }
        return shipType.name().toString() + ":" + escortShipType.name().toString();
    }

    private Map<String, BattleFiledElementConfig> toMapBattleFieldElementsConfig(BattleFieldLineUpConfig config) {
        Map<String, BattleFiledElementConfig> result = new HashMap<>();
        for (BattleFiledRowsConfig rowsConfig : config.getRows()) {
            for (BattleFiledElementConfig elementConfig : rowsConfig.getElements()) {
                result.put(builtKeyShip(elementConfig.getShipType(), elementConfig.getEscortShipType()), elementConfig);
            }
        }
        return result;
    }

    private CheckWinBattleResult checkFactionWinBattle(Battle battle) {
        List<ShipLineUp> shipLineUps = lineUpService.findByBattleId(battle.getId());
        List<ShipLineUp> shipLineUpsAttacker = shipLineUps.stream().filter(l -> FactionType.ATTACKER.equals(l.getBattleProfile().getFaction()))
                                                          .collect(
                                                                  Collectors.toList());
        List<ShipLineUp> shipLineUpsDefender = shipLineUps.stream().filter(l -> FactionType.DEFENDER.equals(l.getBattleProfile().getFaction()))
                                                          .collect(
                                                                  Collectors.toList());
        Boolean attackerLose = checkLostBattle(shipLineUpsAttacker, battle.getCheckResult().getAttackerCheckResult());
        Boolean defenderLose = checkLostBattle(shipLineUpsDefender, battle.getCheckResult().getDefenderCheckResult());
        CheckWinBattleResult result = new CheckWinBattleResult().setIsEnd(false);
        if (attackerLose && defenderLose) {
            return result.setIsEnd(true).setFactionTypeWin(FactionType.ATTACKER);

        } else if (attackerLose) {
            return result.setIsEnd(true).setFactionTypeWin(FactionType.DEFENDER);
        } else if (defenderLose) {
            return result.setIsEnd(true).setFactionTypeWin(FactionType.ATTACKER);
        }
        return result;
    }

    private Boolean checkLostBattle(List<ShipLineUp> shipLineUps, FlatCheckResultBattle flatCheckResultBattle) {
        switch (flatCheckResultBattle) {
            case MOTHER_SHIP:
                for (ShipLineUp shipLineUp : shipLineUps) {
                    if (Objects.nonNull(shipLineUp.getMotherShip()) && shipLineUp.getMotherShip().getCurrentHp() > 0) {
                        return false;
                    }
                }
                return true;
            case ALL:
                for (ShipLineUp shipLineUp : shipLineUps) {
                    if (Objects.nonNull(shipLineUp.getMotherShip()) && shipLineUp.getMotherShip().getCurrentHp() > 0) {
                        return false;
                    }
                    List<EscortShipSquad> escortShipSquads = shipLineUp.getEscortShipSquad();
                    for (EscortShipSquad escortShipSquad : escortShipSquads) {
                        if (escortShipSquad.getAmount() - escortShipSquad.getKilled() > 0) {
                            return false;
                        }
                    }
                }
                return true;
            default:
                throw KOSException.of(ErrorCode.FLAT_CHECK_RESULT_BATTLE_NOT_FOUND);
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

    private void updateFinalBattle(Battle battle, FactionType factionTypeWin) {
        if (Objects.nonNull(factionTypeWin)) {
            switch (factionTypeWin) {
                case ATTACKER:
                    battle.setWinner(battle.getAttacker());
                    battle.getBattleReport().setWinner(battle.getAttacker())
                          .setLoser(battle.getDefender());
                    break;
                case DEFENDER:
                    battle.setWinner(battle.getDefender());
                    battle.getBattleReport().setWinner(battle.getDefender())
                          .setLoser(battle.getAttacker());
                    break;
                default:
                    break;
            }
        }
        battle.setStatus(BattleStatus.END);
        battle.getBattleReport().setEndAt(LocalDateTime.now());
    }

    private void checkItemUse(Attack attack) {
        Map<Long, Belligerent> belligerentMap = attack.getBelligerentJoined();
        for (Map.Entry<Long, Belligerent> entry : belligerentMap.entrySet()) {
            entry.getValue().setItemIds(getItemUseByKosProfile(entry.getKey()));
        }
    }

    private List<ItemId> getItemUseByKosProfile(Long kosProfileId) {
        // fake data todo ipl late
        List<ItemId> result = new ArrayList<>();
        result.add(ItemId.LE_1);
        result.add(ItemId.LE_2);
        result.add(ItemId.LE_3);
        return result;
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

    @Transactional
    public List<Battle> findDefendInProgressBattle(KosProfile kosProfile) {
        return battleRepository.findByKosProfileIdAndBattleStatusAndFaction(kosProfile.getId(),
                                                                            List.of(BattleStatus.INIT, BattleStatus.PROGRESS, BattleStatus.BREAK),
                                                                            FactionType.DEFENDER);
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
        return new ShipLineUpBattle().setShipLineupAttacker(sortShipLineup(shipLineUpsAttacker))
                                     .setShipLineupDefender(sortShipLineup(shipLineUpsDefender));
    }

    private List<ShipLineUp> sortShipLineup(List<ShipLineUp> shipLineUps) {
        return shipLineUps.stream().sorted(Comparator.comparing(ShipLineUp::getTimeJoinedBattle)).collect(Collectors.toList());
    }

    private RoundReport updateRoundReport(RoundReport roundReport, Attack attacker, Attack defender) {
        Map<Long, BattleProfile> maps = getMapBattleProfile(attacker, defender);
        BattleRound battleRound = roundReport.getRound();
        BattleRoundSnapshot roundSnapshotBefore = battleRoundSnapShotService.getByBattleIdAndRound(battleRound.getBattle().getId(),
                                                                                                   battleRound.getIndex() - 1).orElse(null);
        // damage report
        roundReport.setAttackerDamageReport(createDamageReport(attacker))
                   .setDefenderDamageReport(createDamageReport(defender));

        // createMotherShipReport and reverseMotherShipReport
        createMotherShipReport(attacker, maps, roundReport, FactionType.ATTACKER);
        createMotherShipReport(defender, maps, roundReport, FactionType.DEFENDER);

        // createEscortShipReport and reverseMotherShipReport
        createEscortShipReport(attacker, roundSnapshotBefore, roundReport, FactionType.ATTACKER);
        createEscortShipReport(defender, roundSnapshotBefore, roundReport, FactionType.DEFENDER);

        // create AttackUseItem
        createAttackUserItem(attacker, maps, roundReport, FactionType.ATTACKER);
        createAttackUserItem(defender, maps, roundReport, FactionType.DEFENDER);

        return roundReportRepository.save(roundReport);
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

    private DamageReport createDamageReport(Attack attack) {
        DamageReport damageReport = new DamageReport();
        damageReport.setEscortShip(attack.getTotalEscortShipJoined())
                    .setMotherShip(attack.getTotalMotherShipJoined())
                    .setPhysicalAttack(attack.getTotalAtk1Dealt())
                    .setFirePower(attack.getTotalAtk2Dealt())
                    .setTakenPhysicalAttack(attack.getTotalAtk1Taken())
                    .setTakenFirePower(attack.getTotalAtk2Taken())
                    .setEscortShipLost(attack.getTotalEscortShipKilled())
                    .setMotherShipLost(attack.getTotalMotherShipLost());

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
        List<ReverseMotherShipReport> reverseMotherShipReports = new ArrayList<>();
        String key = builtKeyShip(ShipType.MOTHER_SHIP, null);
        if (attack.getBattleFields().containsKey(key)) {
            List<BattleUnit> motherShip = attack.getBattleFields().get(key);
            if (!motherShip.isEmpty()) {
                long index = 0;
                for (BattleUnit shipBattle : motherShip) {
                    MotherShipBattle motherShipBattle = (MotherShipBattle) shipBattle;
                    if (motherShipBattle.getFighting() <= 0) {
                        ReverseMotherShipReport reverseMotherShipReport = new ReverseMotherShipReport(factionType);
                        reverseMotherShipReport.setQuality(motherShipBattle.getQuality())
                                               .setBattleProfile(maps.get(motherShipBattle.getBattleProfileId()))
                                               .setModelId(motherShipBattle.getModelId())
                                               .setRoundReport(roundReport);
                        reverseMotherShipReports.add(reverseMotherShipReport);
                    } else {
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
        reverseMotherShipReportRepository.saveAll(reverseMotherShipReports);
    }

    private void createEscortShipReport(Attack attack, BattleRoundSnapshot roundSnapshotBefore, RoundReport roundReport, FactionType factionType) {
        // todo update
        List<EscortShipConfig> escortShipConfigs = escortShipConfigService.getAll();
        List<EscortShipReport> escortShipReports = new ArrayList<>();
        List<ReverseEscortShipReport> reverseEscortShipReports = new ArrayList<>();
        BattleRound battleRound = roundReport.getRound();
        Map<String, Long> escortShipFightingLiveBeforeRound = getEscortShipFightingLiveBeforeRound(roundSnapshotBefore, factionType);
        for (var config : escortShipConfigs) {
            String key = builtKeyShip(ShipType.ESCORT_SHIP, config.getType());
            if (attack.getBattleFields().containsKey(key)) {
                List<BattleUnit> shipBattleList = attack.getBattleFields().get(key);

                var totalShipFighting = 0L;
                var totalShipReverse = 0L;
                var totalShipKilled = 0L;
                var modelId = config.getId();
                var shipGroupName = config.getEscortShipGroupConfig().getName();
                var escortShipType = config.getType();

                if (!shipBattleList.isEmpty()) {
                    for (BattleUnit shipBattle : shipBattleList) {
                        totalShipFighting += shipBattle.getFighting();
                        totalShipReverse += shipBattle.getReserve();
                        totalShipKilled += shipBattle.getKilled();
                    }
                }

                // create escortShipReport
                EscortShipReport escortShipReport = new EscortShipReport(factionType);
                escortShipReport.setEscortShipType(escortShipType)
                                .setEscortShipGroupName(shipGroupName)
                                .setLeft(totalShipFighting - totalShipKilled)
                                .setLost(totalShipKilled)
                                .setAdd(Objects.isNull(roundSnapshotBefore) ? 0 :
                                        totalShipFighting - escortShipFightingLiveBeforeRound.getOrDefault(key, 0L))
                                .setModelId(modelId)
                                .setRoundReport(roundReport);
                escortShipReports.add(escortShipReport);

                // escortShipReverseReport
                ReverseEscortShipReport reverseEscortShipReport = new ReverseEscortShipReport(factionType);
                reverseEscortShipReport.setLeft(totalShipReverse)
                                       .setEscortShipType(escortShipType)
                                       .setEscortShipGroupName(shipGroupName)
                                       .setRoundReport(roundReport)
                                       .setModelId(modelId);
                reverseEscortShipReports.add(reverseEscortShipReport);
            }
        }
        escortShipReportRepository.saveAll(escortShipReports);
        reverseEscortShipReportRepository.saveAll(reverseEscortShipReports);
    }

    private Map<String, Long> getEscortShipFightingLiveBeforeRound(BattleRoundSnapshot snapshot, FactionType factionType) {
        Map<String, List<BattleUnit>> maps = new HashMap<>();
        if (Objects.nonNull(snapshot)) {
            switch (factionType) {
                case ATTACKER:
                    maps = snapshot.getAttackerModel().getBattleFields();
                    break;
                case DEFENDER:
                    maps = snapshot.getDefenderModel().getBattleFields();
                    break;
                default:
                    break;
            }
        }
        Map<String, Long> mapsResult = new HashMap<>();
        for (EscortShipType escortShipType : EscortShipType.values()) {
            Long shipFightingLive = 0L;
            String key = builtKeyShip(ShipType.ESCORT_SHIP, escortShipType);
            if (maps.containsKey(key)) {
                List<BattleUnit> shipBattleList = maps.get(key);
                for (BattleUnit shipBattle : shipBattleList) {
                    shipFightingLive += shipBattle.getFighting() - shipBattle.getKilled();
                }

            }
            mapsResult.put(key, shipFightingLive);
        }
        return mapsResult;
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

    private final ItemRepository itemRepository;

    private List<Item> getListItemById(List<ItemId> ids) {
        return itemRepository.findByIdIn(ids);
    }

    /**
     * calculate reward & final report and withdraw line up
     */
    @Transactional
    public void onBattleEnded(Battle battle, FactionType factionTypeWin) {
        log.info("Battle:{},on end battle in round: {} , win {} ", battle.getId(), battle.getCurrentRound(), factionTypeWin);
        if (BattleStatus.END.equals(battle.getStatus())) {
            return;
        }
        updateFinalBattle(battle, factionTypeWin);
        calculateBattleFinalReport(battle);
        battleReportRepository.save(battle.getBattleReport());
        battle = battleRepository.save(battle);

        withdrawTroopOccupy(battle, factionTypeWin);
        withdrawTroopLiberate(battle, factionTypeWin);

        log.info("Update sea map element : pvp");
        updateBattleField(battle.getBattleField());

        // update warInfo AfterBattle
        updateWarInfoAfterBattle(battle);

        // send notification
        battleReportService.sendBattleReport(battle.getBattleReport());

        // change base status to peace and send notification for occupied user
        if (Objects.isNull(factionTypeWin) || FactionType.ATTACKER.equals(factionTypeWin)) {
            mapService.changeElementStatusToPeace(battle.getBattleField());
        }
    }

    private void updateBattleField(SeaElement battleField) {
        battleField.setBattle(null);
        ((UserBase) battleField).setInitLiberateAt(null);
        mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(battleField));
    }

    private void withdrawTroopLiberate(Battle battle, FactionType winner) {
        List<BattleProfile> battleProfiles = battle.getBattleProfiles().stream()
                                                   .filter(b -> FactionType.ATTACKER.equals(b.getFaction()))
                                                   .filter(b -> !b.getId().equals(battle.getAttacker().getId()))
                                                   .collect(Collectors.toList());
        for (BattleProfile battleProfile : battleProfiles) {
            withdrawUserAllLineUp(battleProfile.getKosProfile(), battle);
        }
        withdrawTroopOfBase(battle.getAttacker());
    }

    private void withdrawTroopOccupy(Battle battle, FactionType winner) {
        List<BattleProfile> battleProfiles = battle.getBattleProfiles().stream().filter(b -> FactionType.DEFENDER.equals(b.getFaction())).collect(
                Collectors.toList());
        List<ShipLineUp> shipLineUps = new ArrayList<>();
        for (BattleProfile battleProfile : battleProfiles) {
            shipLineUps.addAll(battleProfile.getShipLineUps());
        }
        Long kosProfileOccupy = battle.getDefender().getKosProfile().getId();
        if (Objects.nonNull(winner) && FactionType.DEFENDER.equals(winner)) {
            List<SeaActivity> seaActivityOccupy = new ArrayList<>();
            List<SeaActivity> seaActivityWithdraw = new ArrayList<>();
            for (ShipLineUp shipLineUp : shipLineUps) {
                if (shipLineUp.getActivity().getKosProfile().getId().equals(kosProfileOccupy) && shipLineUp.getMotherShip().getCurrentHp() > 0) {
                    seaActivityOccupy.add(shipLineUp.getActivity());
                } else {
                    seaActivityWithdraw.add(shipLineUp.getActivity());
                }
            }
            seaActivityService.anchorShip(seaActivityOccupy, battle.getBattleField());
            for (SeaActivity seaActivity : seaActivityWithdraw) {
                seaActivityService.withdraw(new WithdrawActivityCommand().setId(seaActivity.getId()));
            }
        } else {
            for (BattleProfile battleProfile : battleProfiles) {
                withdrawUserAllLineUp(battleProfile.getKosProfile(), battle);
            }
        }
    }

    private void withdrawTroopOfBase(BattleProfile battleProfile) {
        List<ShipLineUp> lineUps = battleProfile.getShipLineUps();
        for (int i = 0; i < lineUps.size(); i++) {
            var lineUp = lineUps.get(i);
            lineUpService.onFinishMission(lineUp);
        }
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
                         .setMotherShipHpLost(battleFinalReport.getMotherShipHpLost() + damageReportRound.getTotalHpMotherShipLost());
        return battleFinalReport;

    }

    private boolean withdrawListMotherShips(Collection<MotherShip> motherShips) {
        boolean canWithdrawAll = true;
        for (MotherShip motherShip : motherShips) {
            try {
                // TODO sometime, mother ship's active line up is null
                seaActivityService.withdraw(new WithdrawActivityCommand().setId(motherShip.getActiveLineUp().getActivity().getId()));
            } catch (KOSException e) {
                canWithdrawAll = false;
            }
        }
        return canWithdrawAll;
    }

    private boolean withdrawUserAllLineUp(KosProfile kosProfile, Battle battle) {
        Collection<MotherShip> motherShips = getMotherShipJoinInBattle(kosProfile, battle);
        return withdrawListMotherShips(motherShips);
    }

    private void withdrawAllLineUp(Battle battle) {
//        var participants = battle.getBattleProfiles()
//                                 .stream()
//                                 .filter(battleProfile -> !battleProfile.getId().equals(battle.getDefender().getId()))
//                                 .collect(Collectors.toList());
        UserBase userBase = (UserBase) battle.getBattleField();
        var participants = battle.getBattleProfiles()
                                 .stream()
                                 .filter(battleProfile -> !battleProfile.getKosProfile().getId().equals(userBase.getKosProfile().getId()))
                                 .collect(Collectors.toList());
        for (BattleProfile participant : participants) {
            withdrawUserAllLineUp(participant.getKosProfile(), battle);
        }

    }

    public void sendBattleTaskToQueue(Battle battle) throws JsonProcessingException {
        if (BattleStatus.getStatusIgnore().contains(battle.getStatus())) {
            return;
        }
        BattleTimeConfigDetail battleTimeConfig = getBattleTimeConfigDetail();
        var duration = 0L;
        switch (battle.getStatus()) {
            case BREAK:
                duration = battleTimeConfig.getBreakDuration() * 1000;
                break;
            case PROGRESS:
                duration = battleTimeConfig.getProgressDuration() * 1000;
                break;
            case INIT:
                duration = battleTimeConfig.getInitDuration() * 1000;
                break;
        }
        var task = new BattleTask().setBattleId(battle.getId())
                                   .setDefenderName(null)
                                   .setBattleType(battle.getBattleType())
                                   .setBattleFieldId(battle.getBattleField().getId());
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

    /**
     * Get all battle is moving to userBase
     */
    public List<Battle> getMovingBattleByUserBase(Long userBaseId) {
        return battleRepository.findByUserBaseIdAndStatus(userBaseId, List.of(BattleStatus.INIT));
    }

    public List<Battle> saveAll(List<Battle> battles) {
        return battleRepository.saveAll(battles);
    }

    @Transactional
    public void joinBattle(Battle battle, BattleProfile battleProfileAlly, SeaActivity seaActivity) {
        BattleTimeConfigDetail battleTimeConfig = getBattleTimeConfigDetail();
        if (battle.getStatus().equals(BattleStatus.PROGRESS)) {
            seaActivityService.updateCurrentActionStatus(seaActivity, null, SeaActivityStatus.WAITING);
        } else if (battle.getStatus().equals(BattleStatus.BREAK)) {
            seaActivityService.updateCurrentActionStatus(seaActivity,
                                                         battle.getTimeUpdateStatus().plus(battleTimeConfig.getBreakDuration(), ChronoUnit.SECONDS),
                                                         SeaActivityStatus.BREAK);
        }
        BattleProfile battleProfile = battleProfileService.findOrCreateBattleProfile(battle, battleProfileAlly, seaActivity.getKosProfile());
        ShipLineUp shipLineUp = seaActivity.getLineUp();
        if (Objects.isNull(shipLineUp.getBattleProfile())) {
            shipLineUp.setBattleProfile(battleProfile).setTimeJoinedBattle(LocalDateTime.now());
            lineUpService.save(shipLineUp);
        } else {
            throw KOSException.of(ErrorCode.BAD_REQUEST_ERROR);
        }
        if (battleProfileAlly.getKosProfile().getId().equals(seaActivity.getKosProfile().getId())) {
            battleReportServiceAsyncTask.sendUserJoinForceNotification(seaActivity.getKosProfile().getUser().getId());
        } else {
            battleReportServiceAsyncTask.sendAllyForceNotification(battleProfileAlly.getKosProfile().getUser().getId(), seaActivity);
        }
    }

    public void cancelBattle(UserBase occupiedBase) {
        if (Objects.nonNull(occupiedBase.getInitLiberateAt())) {
            occupiedBase.setInitLiberateAt(null);
            mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(occupiedBase));
            seaActivityAsyncTask.sendQueryBattleStatusNotification(occupiedBase.getInvader().getKosProfileInvader().getUser().getId());
        }
    }

    public List<MotherShip> getMotherShipJoinInBattle(KosProfile kosProfile, Battle battle) {
        var nullableBattleProfile = battle.getBattleProfiles()
                                          .stream()
                                          .filter(profile -> profile.getKosProfile().getId().equals(kosProfile.getId())).findFirst();
        if (nullableBattleProfile.isEmpty()) {
            throw KOSException.of(ErrorCode.USER_HAS_YET_TO_JOIN_BATTLE);
        }
        BattleProfile battleProfile = nullableBattleProfile.get();
        Collection<ShipLineUp> lineUps = battleProfile.getShipLineUps();
        List<MotherShip> motherShips = lineUps.stream().map(ShipLineUp::getMotherShip).collect(Collectors.toList());
        return motherShips;
    }

    private void staticWarInfoAfterRound(BattleRoundSnapshot snapshot) {
        List<KosWarInfo> warInfoAttacker = getListWarInfo(snapshot.getAttackerModel());
        List<KosWarInfo> warInfoDefender = getListWarInfo(snapshot.getDefenderModel());
        //attacker
        updateWarInfo(warInfoAttacker, snapshot.getDefenderModel());
        // defender
        updateWarInfo(warInfoDefender, snapshot.getAttackerModel());
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
        KosWarInfo defenderWarInfo = kosWarInfoService.getByKosProfileId(battle.getDefender().getKosProfile().getId());
        if (Objects.nonNull(battle.getWinner())) {
            if (battle.getWinner().getId().equals(battle.getAttacker().getId())) {
                attackerWarInfo.setWin(attackerWarInfo.getWin() + 1);
                defenderWarInfo.setLose(defenderWarInfo.getLose() + 1);
            } else {
                defenderWarInfo.setWin(defenderWarInfo.getWin() + 1);
                attackerWarInfo.setLose(attackerWarInfo.getLose() + 1);
            }
        } else {
            attackerWarInfo.setLose(attackerWarInfo.getLose() + 1);
            defenderWarInfo.setLose(defenderWarInfo.getLose() + 1);
        }
    }

    private BattleTimeConfigDetail getBattleTimeConfigDetail() {
        return kosConfigService.getBattleTimeConfig().getLiberate();
    }
}
