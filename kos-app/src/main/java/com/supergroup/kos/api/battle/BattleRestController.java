package com.supergroup.kos.api.battle;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.asset.service.AssetService;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetElementByCoordinatesCommand;
import com.supergroup.kos.building.domain.command.GetEscortShipCommand;
import com.supergroup.kos.building.domain.command.SaveOrUpdateElementCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.BattleProfileType;
import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.constant.battle.BattleType;
import com.supergroup.kos.building.domain.constant.battle.ShipStatisticType;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.dto.seamap.CoordinatesDTO;
import com.supergroup.kos.building.domain.dto.seamap.EscortSquadDTO;
import com.supergroup.kos.building.domain.dto.seamap.MoveSessionDTO;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.battle.BattleRoundSnapshot;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.ResourceIsland;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.ShipElement;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;
import com.supergroup.kos.building.domain.model.ship.EscortShip;
import com.supergroup.kos.building.domain.model.ship.MotherShip;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRoundRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.MoveSessionRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaActivityRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementConfigRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementRepository;
import com.supergroup.kos.building.domain.service.battle.BattleLiberateService;
import com.supergroup.kos.building.domain.service.battle.BattlePvPService;
import com.supergroup.kos.building.domain.service.battle.BattleReportService;
import com.supergroup.kos.building.domain.service.config.KosConfigService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.seamap.KosWarInfoService;
import com.supergroup.kos.building.domain.service.seamap.MapService;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;
import com.supergroup.kos.building.domain.service.seamap.activity.LineUpService;
import com.supergroup.kos.building.domain.service.ship.EscortShipService;
import com.supergroup.kos.building.domain.service.ship.MotherShipService;
import com.supergroup.kos.dto.PageResponse;
import com.supergroup.kos.dto.battle.BattleInfoResponse;
import com.supergroup.kos.dto.battle.BattleProfileResponse;
import com.supergroup.kos.dto.battle.BattleReportDetailResponse;
import com.supergroup.kos.dto.battle.BattleReportResponse;
import com.supergroup.kos.dto.battle.BattleStatusResponse;
import com.supergroup.kos.dto.battle.DeleteBattleReportByIdsRequest;
import com.supergroup.kos.dto.battle.RoundReportDetailsResponse;
import com.supergroup.kos.dto.battle.RoundReportResponse;
import com.supergroup.kos.mapper.MotherShipMapper;
import com.supergroup.kos.mapper.WeaponMapper;
import com.supergroup.kos.mapper.WeaponSetMapper;
import com.supergroup.kos.mapper.battle.BattleInfoMapper;
import com.supergroup.kos.mapper.battle.BattleReportMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/battle")
@RequiredArgsConstructor
@Slf4j
public class BattleRestController {

    private final KosProfileService                    kosProfileService;
    private final BattleReportService                  battleReportService;
    private final BattleReportMapper                   battleReportMapper;
    private final AssetService                         assetService;
    private final BattlePvPService                     battlePvPService;
    private final BattleInfoMapper                     battleInfoMapper;
    private final UserBaseService                      userBaseService;
    private final MapService                           mapService;
    private final MotherShipService                    motherShipService;
    private final MotherShipMapper                     motherShipMapper;
    private final WeaponMapper                         weaponMapper;
    private final WeaponSetMapper                      weaponSetMapper;
    private final LineUpService                        lineUpService;
    private final KosWarInfoService                    kosWarInfoService;
    private final EscortShipService                    escortShipService;
    private final SeaElementConfigRepository           seaElementConfigRepository;
    private final KosConfigService                     kosConfigService;
    private final SeaActivityRepository                seaActivityRepository;
    private final BattleLiberateService                battleLiberateService;
    private final MoveSessionRepository                moveSessionRepository;
    private final SeaElementService                    seaElementService;
    private final SeaElementRepository<ResourceIsland> resourceIslandRepo;
    private final BattleRepository                     battleRepository;
    private final BattleRoundRepository                battleRoundRepository;

    @GetMapping("/status")
    public ResponseEntity<BattleStatusResponse> beAttacked() {
        try {
            var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
            var userBase = kosProfile.getBase();
            var attackers = new ArrayList<BattleProfileResponse>();
            // get all active user activity
            List<SeaActivity> activities = seaActivityRepository.findActiveActivities(kosProfile.getId());
            // filter to get element which user activity is anchoring on
            Set<SeaElement> elements = activities.stream()
                                                 .map(SeaActivity::getStationAt)
                                                 .filter(Objects::nonNull)
                                                 .collect(Collectors.toSet());
            elements.add(userBase);
            var liberateTimeConfig = kosConfigService.getBattleTimeConfig().getLiberate();

            List<MoveSession> attackerMoves = new ArrayList<>();
            for (SeaElement element : elements) {
                try {
                    // get all move session which is moving to this element
                    attackerMoves.addAll(moveSessionRepository.findMovesToTarget(element.getId(), kosProfile.getId()));
                    // get liberate combat
                    if (element.type().equals(SeaElementType.USER_BASE)) {
                        UserBase occupiedBase = (UserBase) element;
                        if (Objects.nonNull(occupiedBase.getInitLiberateAt())) {
                            BattleProfileResponse attacker = new BattleProfileResponse();
                            attacker.setType(BattleProfileType.USER)
                                    .setBattleFieldName(occupiedBase.name())
                                    .setKosProfileId(occupiedBase.getKosProfile().getId())
                                    .setUserId(occupiedBase.getKosProfile().getUser().getId())
                                    .setPrepareDuration(liberateTimeConfig.getInitDuration())
                                    .setBattleFieldCoordinates(new CoordinatesDTO().setX(occupiedBase.getX()).setY(occupiedBase.getY()))
                                    .setStartAt(occupiedBase.getInitLiberateAt().plus(liberateTimeConfig.getInitDuration(), ChronoUnit.SECONDS))
                                    .setBattleType(BattleType.LIBERATE);
                            attackers.add(attacker);
                        }
                    }
                    // get battle that user joins as defender
                    if (Objects.nonNull(element.getBattle())) {
                        Battle battle = element.getBattle();
                        if (battle.getBattleType().equals(BattleType.LIBERATE)) {
                            continue;
                        }
                        if (battle.getDefender().getType().equals(BattleProfileType.USER)
                            && battle.getDefender().getKosProfile().getId().equals(kosProfile.getId())) {
                            KosProfile kosAttacker = battle.getAttacker().getKosProfile();
                            UserBase kosAttackerBase = kosAttacker.getBase();
                            BattleProfileResponse attacker = new BattleProfileResponse();
                            attacker.setType(BattleProfileType.USER)
                                    .setBattleFieldName(battle.getBattleField().name())
                                    .setKosProfileId(kosAttacker.getId())
                                    .setUserId(kosAttacker.getUser().getId())
                                    .setBattleType(battle.getBattleType())
                                    .setMoveSession(new MoveSessionDTO()
                                                            .setStart(kosAttackerBase.getCoordinates())
                                                            .setEnd(battle.getBattleSite())
                                                            .setSpeed(Double.MAX_VALUE)
                                                            .setDestinationElementId(element.getId())
                                                            .setDestinationType(element.getSeaElementConfig().getType())
                                                            .setTimeStart(LocalDateTime.now()));

                            attackers.add(attacker);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // ignore exception
                }
            }
            // filter move session is invalid
            attackerMoves = attackerMoves.stream().filter(e -> {
                                             // check coordinate
                                             Coordinates destinationOnMap = e.getEnd();
                                             var cachedElement = mapService.getElementByCoordinates(
                                                                                   new GetElementByCoordinatesCommand(destinationOnMap))
                                                                           .stream()
                                                                           .filter(SeaElement::getActive)
                                                                           .filter(element -> element.getId().equals(e.getDestinationElementId()))
                                                                           .filter(element -> !(element instanceof ShipElement))
                                                                           .findFirst().orElse(null);
                                             // if element is not found
                                             // notify user no target found
                                             // then withdraw
                                             return !Objects.isNull(cachedElement);
                                         }).filter(mv -> {
                                             if (Objects.isNull(mv.getBattleId())) {return true;}
                                             Battle battle = battleRepository.getById(mv.getBattleId());
                                             if (battle.getStatus().equals(BattleStatus.END)) {return false;}
                                             return true;
                                         }).
                                         collect(Collectors.toList());
            // get attacker info from move session
            for (MoveSession moveSession : attackerMoves) {
                try {
                    KosProfile enemyKosProfile = moveSession.getSeaActivity().getKosProfile();
                    BattleProfileResponse attacker = new BattleProfileResponse();
                    var destination = seaElementService.findElementById(moveSession.getDestinationElementId());
                    attacker.setType(BattleProfileType.USER)
                            .setKosProfileId(enemyKosProfile.getId())
                            .setBattleFieldName(destination.name())
                            .setUserId(enemyKosProfile.getUser().getId())
                            .setBattleType(getBattleType(moveSession))
                            .setMoveSession(battleReportMapper.map(moveSession, resourceIslandRepo));
                    attackers.add(attacker);
                } catch (Exception e) {
                    e.printStackTrace();
                    // ingore
                }
            }
            for (BattleProfileResponse attacker : attackers) {
                try {
                    if (attacker.getBattleType().equals(BattleType.LIBERATE)) {
                        continue;
                    }
                    MoveSessionDTO moveSessionDTO = attacker.getMoveSession();
                    SeaElementType destinationType = moveSessionDTO.getDestinationType();
                    Long destinationElementId = moveSessionDTO.getDestinationElementId();
                    Coordinates end = moveSessionDTO.getEnd();
                    SeaElement destinationElement = seaElementService.getElementById(destinationElementId);
                    // if move session destination is not move session end, ignore
                    if (!Objects.equals(destinationElement.getX(), end.getX())
                        || !Objects.equals(destinationElement.getY(), end.getY())) {
                        continue;
                    }
                    if (destinationType.equals(SeaElementType.USER_BASE)) {
                        if (destinationElementId.equals(userBase.getId())) {
                            if (Objects.nonNull(destinationElement.getInvader())) {
                                moveSessionDTO.setDestinationType(SeaElementType.OCCUPIED_OWNED_BASE);
                            }
                        } else {
                            if (Objects.nonNull(destinationElement.getInvader())
                                && destinationElement.getInvader()
                                                     .getKosProfileInvader()
                                                     .getId()
                                                     .equals(kosProfile.getId())) {
                                moveSessionDTO.setDestinationType(SeaElementType.OCCUPIED_ENEMY_BASE);
                            } else {
                                moveSessionDTO.setDestinationType(SeaElementType.STATIONED_ENEMY_BASE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // ignore
                }
            }

            try {
                if (Objects.nonNull(userBase.getInitLiberateAt())) {
                    BattleProfileResponse attacker = new BattleProfileResponse();
                    attacker.setType(BattleProfileType.USER)
                            .setBattleFieldName(userBase.getIslandName())
                            .setKosProfileId(userBase.getKosProfile().getId())
                            .setUserId(kosProfile.getUser().getId())
                            .setPrepareDuration(liberateTimeConfig.getInitDuration())
                            .setBattleFieldCoordinates(new CoordinatesDTO().setX(userBase.getX()).setY(userBase.getY()))
                            .setStartAt(userBase.getInitLiberateAt().plus(liberateTimeConfig.getInitDuration(), ChronoUnit.SECONDS))
                            .setBattleType(BattleType.LIBERATE);
                    attackers.add(attacker);
                }
            } catch (Exception e) {
                e.printStackTrace();
                // ignore
            }

            return ResponseEntity.ok(new BattleStatusResponse().setBeingAttacked(!attackers.isEmpty()).setAttackers(attackers));
        } catch (Exception e) {
            e.printStackTrace();
            // return empty list if it has error, ignore error
            return ResponseEntity.ok(new BattleStatusResponse().setBeingAttacked(false).setAttackers(List.of()));
        }
    }

    private BattleType getBattleType(MoveSession moveSession) {
        switch (moveSession.getDestinationType()) {
            case BOSS:
                return BattleType.MONSTER;
            case USER_BASE:
                switch (moveSession.getMissionType()) {
                    case ATTACK:
                        return BattleType.ATTACK;
                    case OCCUPY:
                        return BattleType.OCCUPY;
                }
            case RESOURCE:
                return BattleType.MINE;
        }
        return null;
    }

    @GetMapping("/report")
    public ResponseEntity<PageResponse<BattleReportResponse>> getBattleReport(Pageable pageable) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var reports = battleReportService.findBattleReportByKosProfileId(kosProfile.getId());
        var reportResponses = reports.stream()
                                     .map(battleReport -> battleReportMapper.map(battleReport,
                                                                                 kosProfile,
                                                                                 assetService,
                                                                                 battleRoundRepository,
                                                                                 seaElementConfigRepository))
                                     .collect(Collectors.toList());
        return ResponseEntity.ok(new PageResponse<BattleReportResponse>().setData(reportResponses).setTotal((long) reports.size()));
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<BattleReportResponse> getBattleReportByBattleId(@PathVariable("id") Long battleId) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var report = battleReportService.getBattleReportByKosProfileIdAndBattleId(kosProfile.getId(), battleId);
        var reportResponses = battleReportMapper.map(report, kosProfile, assetService, seaElementConfigRepository);
        return ResponseEntity.ok(reportResponses);
    }

    @DeleteMapping("/{id}/report")
    public ResponseEntity<?> deleteBattleReport(@PathVariable("id") Long battleId) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        battleReportService.deleteBattleReportByBattleId(battleId, kosProfile.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/report")
    public ResponseEntity<?> deleteBattleReportByIds(@RequestBody DeleteBattleReportByIdsRequest request) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        for (Long id : request.getIds()) {
            battleReportService.deleteBattleReportByBattleId(id, kosProfile.getId());
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/report/details")
    public ResponseEntity<BattleReportDetailResponse> getBattleReportDetails(@PathVariable("id") Long id) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var report = battleReportService.getBattleReportByBattleIdAndKosProfileId(id, kosProfile.getId());
        return ResponseEntity.ok(battleReportMapper.map(report, kosConfigService));
    }

    @GetMapping("/{battleId}/round/{id}/report")
    public ResponseEntity<RoundReportResponse> getRoundReport(@PathVariable("id") Long id, @PathVariable("battleId") Long battleId) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var reports = battleReportService.getRoundReportById(id, battleId, kosProfile.getId());
        return ResponseEntity.ok(battleReportMapper.map(reports, assetService));
    }

    @GetMapping("/{battleId}/round/{id}/report/details")
    public ResponseEntity<RoundReportDetailsResponse> getRoundReportDetails(@PathVariable("id") Long id, @PathVariable("battleId") Long battleId) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var reports = battleReportService.getRoundReportById(id, battleId, kosProfile.getId());
        return ResponseEntity.ok(battleReportMapper.toRoundReportDetail(reports));
    }

    @GetMapping("/info/{battleId}")
    public ResponseEntity<BattleInfoResponse> getBattleInfo(@PathVariable("battleId") Long battleId) {
        Battle battle = battlePvPService.findById(battleId).orElseThrow(() -> KOSException.of(ErrorCode.BATTLE_NOT_FOUND));
        if (battle.getStatus().equals(BattleStatus.END)) {
            UserBase userBase = userBaseService.findByCoordinatesAndActive(battle.getBattleSite());
            if (Objects.nonNull(userBase)) {
                mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(userBase.setBattle(null)));
            }
        }
        BattleRoundSnapshot snapshot = battlePvPService.getCurrentRoundBattle(battle).getBattleRoundSnapshot();
        return ResponseEntity.ok(battleInfoMapper.toResponse(battle, snapshot));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<?> withdrawFromBattle(@PathVariable("id") Long battleId,
                                                @RequestParam(name = "motherShipId", required = false) List<Long> motherShipList) {
        Battle battle = battlePvPService.findById(battleId).orElseThrow(() -> KOSException.of(ErrorCode.BATTLE_NOT_FOUND));
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        Collection<MotherShip> motherShipsToWithdraw = new ArrayList<>();
        if (!Objects.isNull(motherShipList) && !motherShipList.isEmpty()) {
            motherShipsToWithdraw = motherShipService.findByIdIn(motherShipList);
        }

        battlePvPService.withdraw(kosProfile, battle, motherShipsToWithdraw);
        return ResponseEntity.ok().build();

    }

    @GetMapping("/{id}/mother-ship")
    public ResponseEntity<?> getMotherShipInBattle(@PathVariable("id") Long battleId) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        KosProfile kosProfile = kosProfileService.getKosProfileById(kosProfileId);
        Battle battle = battlePvPService.getById(battleId);
        var motherShips = battlePvPService.getMotherShipJoinInBattle(kosProfile, battle);
        var response = motherShips.stream().map(motherShip -> {
            var thumbnail = assetService.getUrl(motherShip.getMotherShipConfigQualityConfig().getMotherShipConfig().getThumbnail());
            var res = motherShipMapper.toDTO(motherShip);
            res.getModel().setThumbnail(thumbnail);
            var weaponResponses = motherShip.getWeapons().stream().map(weapon -> {
                var thumbnailWeapon = assetService.getUrl(weapon.getWeaponConfig().getThumbnail());
                var weaponResponse = weaponMapper.toDTO(weapon);
                weaponResponse.getModel().setThumbnail(thumbnailWeapon);
                return weaponResponse;
            }).collect(Collectors.toList());
            var weaponSetResponses = motherShip.getWeaponSets().stream().map(weaponSet -> {
                var thumbnailWeaponSet = assetService.getUrl(weaponSet.getWeaponSetConfig().getThumbnail());
                var weaponResponse = weaponSetMapper.toDTO(weaponSet);
                weaponResponse.getModel().setThumbnail(thumbnailWeaponSet);
                return weaponResponse;
            }).collect(Collectors.toList());
            res.setWeapons(weaponResponses);
            res.setWeaponSets(weaponSetResponses);

            double motherShipPower = kosWarInfoService.getMotherShipPower(motherShip, ShipStatisticType.ATK1);
            List<EscortSquadDTO> lineUpDTOS = lineUpService.getCurrentLineUp(kosProfileId, motherShip.getId());
            for (EscortSquadDTO squad : lineUpDTOS) {
                EscortShip escortShip = escortShipService.getEscortShipByShipType(
                        new GetEscortShipCommand().setShipType(squad.getEscortShipType()).setKosProfileId(kosProfileId));
                motherShipPower += kosWarInfoService.getSingleEscortShipPower(escortShip, ShipStatisticType.ATK1) * squad.getAmount();
            }
            res.setPower(motherShipPower);

            return res;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/liberate")
    public ResponseEntity<?> liberate() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        UserBase userBase = userBaseService.getByKosProfileId(kosProfile.getId());
        if (Objects.nonNull(userBase.getInitLiberateAt())) {
            throw KOSException.of(ErrorCode.IN_PROGRESS_LIBERATING);
        }
        if (Objects.nonNull(userBase.getBattle())) {
            throw KOSException.of(ErrorCode.CAN_NOT_START_BATTLE_WHEN_HAVE_OTHER);
        }
        battleLiberateService.liberate(kosProfile);
        return ResponseEntity.ok().build();
    }
}

