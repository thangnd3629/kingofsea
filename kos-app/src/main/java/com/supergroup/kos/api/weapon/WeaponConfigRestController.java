package com.supergroup.kos.api.weapon;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.asset.service.AssetService;
import com.supergroup.kos.building.domain.repository.persistence.weapon.WeaponConfigDataSource;
import com.supergroup.kos.dto.weapon.WeaponConfigResponse;
import com.supergroup.kos.mapper.WeaponConfigMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/weapon-model")
@RequiredArgsConstructor
public class WeaponConfigRestController {
    private final AssetService       assetService;
    private final WeaponConfigMapper weaponConfigMapper;
    private final WeaponConfigDataSource weaponConfigDataSource;

    @GetMapping("/{id}")
    public ResponseEntity<WeaponConfigResponse> getWeaponConfigById(@PathVariable("id") Long weaponModelId) {
        var weaponConfig = weaponConfigDataSource.getById(weaponModelId);
        var thumbnail = assetService.getUrl(weaponConfig.getThumbnail());
        return ResponseEntity.ok(weaponConfigMapper.toDTO(weaponConfig).setThumbnail(thumbnail));
    }

}
