package com.supergroup.kos.api.ship;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.asset.service.AssetService;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.BuyMotherShipSystemCommand;
import com.supergroup.kos.building.domain.command.EquipWeaponCommand;
import com.supergroup.kos.building.domain.command.EquipWeaponsCommand;
import com.supergroup.kos.building.domain.command.GetEscortShipCommand;
import com.supergroup.kos.building.domain.command.GetMotherShipCommand;
import com.supergroup.kos.building.domain.command.UpgradeMotherShipCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.MotherShipQualityKey;
import com.supergroup.kos.building.domain.constant.battle.ShipStatisticType;
import com.supergroup.kos.building.domain.dto.seamap.EscortSquadDTO;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.ship.EscortShip;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.repository.persistence.ship.MotherShipLevelConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.ship.MotherShipQualityConfigDataSource;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.seamap.KosWarInfoService;
import com.supergroup.kos.building.domain.service.seamap.activity.LineUpService;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;
import com.supergroup.kos.building.domain.service.ship.EscortShipService;
import com.supergroup.kos.building.domain.service.ship.MotherShipService;
import com.supergroup.kos.dto.ship.BuyMotherShipSystemRequest;
import com.supergroup.kos.dto.ship.EquipWeaponRequest;
import com.supergroup.kos.dto.ship.EquipWeaponsRequest;
import com.supergroup.kos.dto.ship.MotherShipResponse;
import com.supergroup.kos.dto.upgrade.UpgradeStatusResponse;
import com.supergroup.kos.mapper.MotherShipMapper;
import com.supergroup.kos.mapper.UpgradeLevelMotherShipMapper;
import com.supergroup.kos.mapper.UpgradeQualityMotherShipMapper;
import com.supergroup.kos.mapper.WeaponMapper;
import com.supergroup.kos.mapper.WeaponSetMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/mother-ship")
@RequiredArgsConstructor
public class MotherShipRestController {

    private final KosProfileService                 kosProfileService;
    private final MotherShipService                 motherShipService;
    private final AssetService                      assetService;
    private final MotherShipLevelConfigDataSource   motherShipLevelConfigDataSource;
    private final MotherShipQualityConfigDataSource motherShipQualityConfigDataSource;
    private final MotherShipMapper                  motherShipMapper;
    private final WeaponMapper                      weaponMapper;
    private final WeaponSetMapper                   weaponSetMapper;
    private final UpgradeLevelMotherShipMapper      upgradeLevelMotherShipMapper;
    private final UpgradeQualityMotherShipMapper    upgradeQualityMotherShipMapper;
    private final LineUpService                     lineUpService;
    private final KosWarInfoService                 kosWarInfoService;
    private final EscortShipService                 escortShipService;
    private final SeaActivityService                seaActivityService;

    // Get Mother Ship
    @GetMapping("")
    @Transactional
    public ResponseEntity<List<MotherShipResponse>> getMotherShips() {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        var motherShips = motherShipService.getMotherShips(new GetMotherShipCommand().setKosProfileId(kosProfileId));
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
            // find and set current location for mother ship
            if (Objects.nonNull(motherShip.getActiveLineUp())
                && Objects.nonNull(motherShip.getActiveLineUp().getActivity())) {
                // if mother ship on duty, current location is activity's location
                SeaActivity activity = motherShip.getActiveLineUp().getActivity();
                SeaElement seaElement = seaActivityService.findElementToReturn(activity);
                res.setReturnLocation(seaElement.getCoordinates());
                if (Objects.nonNull(motherShip.getActiveLineUp().getActivity().getCurrentLocation())) {
                    res.setCurrentLocation(activity.getCurrentLocation());
                }
            } else {
                // if mother ship is not on another duty, current location is base location
                res.setCurrentLocation(motherShip.getCommandBuilding().getKosProfile().getBase().getCoordinates());
            }

            return res;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Get Mother Ship by id
    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<MotherShipResponse> getMotherShip(@PathVariable Long id) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        var motherShip = motherShipService.getMotherShipById(new GetMotherShipCommand().setKosProfileId(kosProfileId).setMotherShipId(id));
        var thumbnail = assetService.getUrl(motherShip.getMotherShipConfigQualityConfig().getMotherShipConfig().getThumbnail());
        var response = motherShipMapper.toDTO(motherShip);
        response.getModel().setThumbnail(thumbnail);
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
        response.setWeapons(weaponResponses);
        response.setWeaponSets(weaponSetResponses);

        double motherShipPower = kosWarInfoService.getMotherShipPower(motherShip, ShipStatisticType.ATK1);
        List<EscortSquadDTO> lineUpDTOS = lineUpService.getCurrentLineUp(kosProfileId, motherShip.getId());
        for (EscortSquadDTO squad : lineUpDTOS) {
            EscortShip escortShip = escortShipService.getEscortShipByShipType(
                    new GetEscortShipCommand().setShipType(squad.getEscortShipType()).setKosProfileId(kosProfileId));
            motherShipPower += kosWarInfoService.getSingleEscortShipPower(escortShip, ShipStatisticType.ATK1) * squad.getAmount();
        }
        response.setPower(motherShipPower);
        return ResponseEntity.ok(response);
    }

    // Upgrade Level Mother Ship
    @PutMapping("/{id}/upgrade-level")
    public ResponseEntity<?> upgradeLevelMotherShip(@PathVariable Long id) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        motherShipService.upgradeLevelMotherShip(new UpgradeMotherShipCommand().setMotherShipId(id).setKosProfileId(kosProfileId));
        return ResponseEntity.accepted().build();
    }

    // Get Upgrade Level Info
    @GetMapping("/upgrade-level")
    public ResponseEntity<?> getUpgradeLevelInfo(@RequestParam(value = "level", required = false) Long level) {
        if (Objects.isNull(level)) {
            var configs = motherShipLevelConfigDataSource.getAll();
            return ResponseEntity.ok(upgradeLevelMotherShipMapper.toDTOs(configs));
        } else {
            var config = motherShipLevelConfigDataSource.getByLevel(level);
            return ResponseEntity.ok(upgradeLevelMotherShipMapper.toDTO(config));
        }
    }

    // Get Upgrade Level Status
    @GetMapping("{id}/upgrade-level/status")
    public ResponseEntity<UpgradeStatusResponse> upgradeLevelStatus(@PathVariable Long id) {
        var kosProfileId = kosProfileService.findByUserId(AuthUtil.getUserId())
                                            .orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND)).getId();
        var motherShip = motherShipService.getMotherShipById(new GetMotherShipCommand().setMotherShipId(id).setKosProfileId(kosProfileId));
        if (Objects.nonNull(motherShip.getUpgradeLevel())) {
            UpgradeSession upgradeSession = motherShip.getUpgradeLevel();
            return ResponseEntity.ok(new UpgradeStatusResponse().setUpgradeSessionId(upgradeSession.getId())
                                                                .setDuration(upgradeSession.getDuration())
                                                                .setCurrent(
                                                                        Duration.between(upgradeSession.getTimeStart(),
                                                                                         LocalDateTime.now())
                                                                                .toMillis()));
        } else {
            throw KOSException.of(ErrorCode.MOTHER_SHIP_NOT_IN_UPGRADING);
        }
    }

    // Upgrade Quality Mother Ship
    @PutMapping("/{id}/upgrade-quality")
    public ResponseEntity<?> upgradeQualityMotherShip(@PathVariable Long id) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        motherShipService.upgradeQualityMotherShip(new UpgradeMotherShipCommand().setMotherShipId(id).setKosProfileId(kosProfileId));
        return ResponseEntity.accepted().build();
    }

    // Get Upgrade quality info
    @GetMapping("/upgrade-quality")
    public ResponseEntity<?> getUpgradeQualityInfo(@RequestParam(value = "quality", required = false) MotherShipQualityKey quality) {
        if (Objects.isNull(quality)) {
            var configs = motherShipQualityConfigDataSource.getAll();
            return ResponseEntity.ok(upgradeQualityMotherShipMapper.toDTOs(configs));
        } else {
            var config = motherShipQualityConfigDataSource.getByQuality(quality);
            return ResponseEntity.ok(upgradeQualityMotherShipMapper.toDTO(config));
        }
    }

    // Get Upgrade Level Status
    @GetMapping("{id}/upgrade-quality/status")
    @Transactional
    public ResponseEntity<UpgradeStatusResponse> upgradeQualityStatus(@PathVariable Long id) {
        var kosProfileId = kosProfileService.findByUserId(AuthUtil.getUserId())
                                            .orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND)).getId();
        var motherShip = motherShipService.getMotherShipById(new GetMotherShipCommand().setMotherShipId(id).setKosProfileId(kosProfileId));
        if (Objects.nonNull(motherShip.getUpgradeQuality())) {
            UpgradeSession upgradeSession = motherShip.getUpgradeQuality();
            return ResponseEntity.ok(new UpgradeStatusResponse().setUpgradeSessionId(upgradeSession.getId())
                                                                .setDuration(upgradeSession.getDuration())
                                                                .setCurrent(
                                                                        Duration.between(upgradeSession.getTimeStart(),
                                                                                         LocalDateTime.now())
                                                                                .toMillis()));
        } else {
            throw KOSException.of(ErrorCode.BUILDING_NOT_IN_UPGRADING);
        }
    }

    // Equip and remove Weapon by id weapon
    @PutMapping("/{motherShipId}/weapon/{weaponId}")
    public ResponseEntity<?> equipWeapon(@PathVariable Long motherShipId, @PathVariable Long weaponId,
                                         @Valid @RequestBody EquipWeaponRequest request) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        motherShipService.equipWeapon(
                new EquipWeaponCommand()
                        .setIsEquipping(request.getIsEquipping())
                        .setIsWeaponSet(request.getIsWeaponSet())
                        .setWeaponId(weaponId)
                        .setKosProfileId(kosProfileId)
                        .setMotherShipId(motherShipId));
        return ResponseEntity.accepted().build();
    }

    // update weapon and weapon set on mother ship
    @PutMapping("/{motherShipId}/weapons")
    public ResponseEntity<?> equipWeapon(@PathVariable Long motherShipId,
                                         @Valid @RequestBody EquipWeaponsRequest request) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        motherShipService.equipWeapons(
                new EquipWeaponsCommand().setKosProfileId(kosProfileId)
                                         .setMotherShipId(motherShipId)
                                         .setWeaponIds(request.getWeaponIds())
                                         .setWeaponSetIds(request.getWeaponSetIds()));
        return ResponseEntity.accepted().build();
    }

    // By mother ship system
    @PostMapping("")
    public ResponseEntity<MotherShipResponse> buyMotherShipSystem(@Valid @RequestBody BuyMotherShipSystemRequest request) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        var motherShip = motherShipService.buyMotherShipFromSystemStore(
                new BuyMotherShipSystemCommand()
                        .setMotherShipModelId(request.getModelId())
                        .setKosProfileId(kosProfileId));
        var thumbnail = assetService.getUrl(motherShip.getMotherShipConfigQualityConfig().getMotherShipConfig().getThumbnail());
        var response = motherShipMapper.toDTO(motherShip);
        response.getModel().setThumbnail(thumbnail);
        return ResponseEntity.ok(response);
    }
}
