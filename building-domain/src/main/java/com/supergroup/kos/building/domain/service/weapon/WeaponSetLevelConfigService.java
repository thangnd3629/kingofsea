package com.supergroup.kos.building.domain.service.weapon;

import java.util.List;

import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.constant.WeaponSetLevel;
import com.supergroup.kos.building.domain.model.config.WeaponSetLevelConfig;
import com.supergroup.kos.building.domain.repository.persistence.weapon.WeaponSetLevelConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.weapon.WeaponSetLevelConfigRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeaponSetLevelConfigService {

    private final WeaponSetLevelConfigDataSource weaponSetLevelConfigDataSource;
    private final WeaponSetLevelConfigRepository weaponSetLevelConfigRepository;

    public List<WeaponSetLevelConfig> getWeaponSetLevelConfigs() {
        return weaponSetLevelConfigDataSource.getAll();
    }

    public WeaponSetLevelConfig getWeaponSetLevelConfigByLevel(WeaponSetLevel level) {
        return weaponSetLevelConfigRepository.findByLevel(level).orElseThrow(() -> KOSException.of(ErrorCode.WEAPON_SET_LEVEL_CONFIG_IS_NOT_FOUND));
    }

    public List<WeaponSetLevelConfig> getByArmoryBuildingConfigId(Long id) {
        return weaponSetLevelConfigRepository.findByArmoryBuildingConfigId(id);

    }

}
