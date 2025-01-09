package com.supergroup.kos.building.domain.repository.persistence.weapon;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.model.config.WeaponSetLevelConfig;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WeaponSetLevelConfigDataSource {

    private final WeaponSetLevelConfigRepository weaponSetLevelConfigRepository;

    @Cacheable(cacheNames = "WeaponSetLevelConfig", key = "'List'")
    public List<WeaponSetLevelConfig> getAll() {
        return weaponSetLevelConfigRepository.findByOrderByLevelAsc();
    }

    @Cacheable(cacheNames = "WeaponSetLevelConfig", key = "#id")
    public WeaponSetLevelConfig getById(Long id) {
        return weaponSetLevelConfigRepository.findById(id)
                                             .orElseThrow(() -> KOSException.of(ErrorCode.WEAPON_SET_LEVEL_CONFIG_IS_NOT_FOUND));
    }

}
