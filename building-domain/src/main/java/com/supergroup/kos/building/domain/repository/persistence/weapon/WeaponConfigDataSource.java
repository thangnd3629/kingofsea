package com.supergroup.kos.building.domain.repository.persistence.weapon;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.model.config.WeaponConfig;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WeaponConfigDataSource {

    private final WeaponConfigRepository weaponConfigRepository;

    @Cacheable(cacheNames = "WeaponConfig", key = "'LIST'")
    public List<WeaponConfig> getAll() {
        return weaponConfigRepository.findAll();
    }

    @Cacheable(cacheNames = "WeaponConfig", key = "#id")
    public WeaponConfig getById(Long id) {
        return weaponConfigRepository.findById(id)
                                     .orElseThrow(() -> KOSException.of(ErrorCode.WEAPON_CONFIG_IS_NOT_FOUND));
    }

}
