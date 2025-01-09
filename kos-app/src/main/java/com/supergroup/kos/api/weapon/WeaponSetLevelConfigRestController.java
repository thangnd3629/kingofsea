package com.supergroup.kos.api.weapon;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.kos.building.domain.constant.WeaponSetLevel;
import com.supergroup.kos.building.domain.service.weapon.WeaponSetLevelConfigService;
import com.supergroup.kos.mapper.WeaponSetLevelConfigMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/weapon-set-level")
@RequiredArgsConstructor
public class WeaponSetLevelConfigRestController {

    private final WeaponSetLevelConfigService weaponSetLevelConfigService;
    private final WeaponSetLevelConfigMapper  weaponSetLevelConfigMapper;

    @GetMapping("")
    public ResponseEntity<?> getWeaponSetLevels(@RequestParam(value = "quality", required = false) WeaponSetLevel level) {
        if (Objects.isNull(level)) {
            var weaponSetLevelConfigs = weaponSetLevelConfigService.getWeaponSetLevelConfigs();
            return ResponseEntity.ok(weaponSetLevelConfigMapper.toDTOs(weaponSetLevelConfigs));
        } else {
            var weaponSetLevelConfig = weaponSetLevelConfigService.getWeaponSetLevelConfigByLevel(level);
            weaponSetLevelConfig.setArmoryLevelRequired(weaponSetLevelConfig.getArmoryBuildingConfig().getLevel());
            return ResponseEntity.ok(weaponSetLevelConfigMapper.toDTO(weaponSetLevelConfig));
        }
    }
}
