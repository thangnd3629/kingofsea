package com.supergroup.kos.building.domain.repository.persistence.weapon;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.model.config.WeaponSetConfig;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WeaponSetConfigDataSource {

    private final WeaponSetConfigRepository weaponSetConfigRepository;

    @Cacheable(cacheNames = "WeaponSetConfig", key = "'LIST'")
    public List<WeaponSetConfig> getAll() {
        return weaponSetConfigRepository.findAll();
    }

    @Cacheable(cacheNames = "WeaponSetConfig", key = "#id")
    public WeaponSetConfig getById(Long id) {
        return weaponSetConfigRepository.findById(id)
                                        .orElseThrow(() -> KOSException.of(ErrorCode.WEAPON_SET_CONFIG_IS_NOT_FOUND));
    }

}
