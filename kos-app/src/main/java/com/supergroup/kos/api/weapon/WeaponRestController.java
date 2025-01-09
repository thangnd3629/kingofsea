package com.supergroup.kos.api.weapon;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.asset.service.AssetService;
import com.supergroup.kos.building.domain.command.GetWeaponByIdCommand;
import com.supergroup.kos.building.domain.command.GetWeaponsCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.model.config.WeaponConfig;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.weapon.WeaponService;
import com.supergroup.kos.dto.weapon.WeaponConfigResponse;
import com.supergroup.kos.dto.weapon.WeaponResponse;
import com.supergroup.kos.mapper.WeaponMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/weapon")
@RequiredArgsConstructor
public class WeaponRestController {
    private final KosProfileService kosProfileService;
    private final WeaponService     weaponService;
    private final AssetService      assetService;
    private final WeaponMapper      weaponMapper;

    @GetMapping("")
    public ResponseEntity<?> getWeapons() {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        var weapons = weaponService.getWeapons(new GetWeaponsCommand().setKosProfileId(kosProfileId));
        var data = weapons.stream().map(w -> {
            var weaponResponse = weaponMapper.toDTO(w);
            var thumbnail = assetService.getUrl(w.getWeaponConfig().getThumbnail());
            weaponResponse.getModel().setThumbnail(thumbnail);
            return weaponResponse;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("data", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WeaponResponse> getWeaponById(@PathVariable("id") Long weaponId) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        var weapon = weaponService.getWeaponById(new GetWeaponByIdCommand().setWeaponId(weaponId).setKosProfileId(kosProfileId));
        var countWeaponModelExist = weaponService.countByWeaponConfigIdAndKosProfileId(weapon.getWeaponConfig().getId(), kosProfileId);
        var thumbnail = assetService.getUrl(weapon.getWeaponConfig().getThumbnail());
        weapon.getWeaponConfig().setThumbnail(thumbnail);
        return ResponseEntity.ok(weaponMapper.toDTO(weapon.setQualityExist(countWeaponModelExist)));
    }
    @GetMapping("/config")
    public ResponseEntity<?> getWeaponConfig(){
        List<WeaponConfig> weaponConfigs = weaponService.getAllConfig();
        Collection<WeaponConfigResponse> results =weaponMapper.toDTOS(weaponConfigs);
        results = results.stream().map(result -> result.setThumbnail(assetService.getUrl(result.getThumbnail()))).collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

}
