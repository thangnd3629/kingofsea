package com.supergroup.kos.building.domain.service.weapon;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.model.config.WeaponSetConfig;
import com.supergroup.kos.building.domain.repository.persistence.weapon.WeaponSetConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.weapon.WeaponSetConfigRepository;
import com.supergroup.kos.building.domain.repository.persistence.weapon.WeaponSetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeaponSetConfigService {
    private final WeaponSetConfigDataSource weaponSetConfigDataSource;
    private final WeaponSetConfigRepository weaponSetConfigRepository;

    private final WeaponSetRepository weaponSetRepository;

    public List<WeaponSetConfig> getWeaponSetConfigs(Long kosProfileId) {
        // @formatter:on
        return weaponSetConfigRepository.findAll()
                                        .stream()
                                        .map(wsc -> {
                                            var count = weaponSetRepository.countByWeaponSetConfigByIdAndKosProfileId(wsc.getId(), kosProfileId);
                                            return wsc.setQualityExist(count);
                                        })
                                        .collect(Collectors.toList());
        // @formatter:off
    }

    public WeaponSetConfig getWeaponSetConfigById(Long kosProfileId, Long weaponSetConfigId) {
        var weaponSetConfig = weaponSetConfigRepository.findById(weaponSetConfigId)
                                                       .orElseThrow(() -> KOSException.of(ErrorCode.WEAPON_SET_CONFIG_IS_NOT_FOUND));
        var count = weaponSetRepository.countByWeaponSetConfigByIdAndKosProfileId(weaponSetConfigId, kosProfileId);
        weaponSetConfig.setQualityExist(count);
        return weaponSetConfig;
    }

    public WeaponSetConfig getWeaponSetConfigById(Long weaponSetConfigId) {
        return weaponSetConfigRepository.findById(weaponSetConfigId)
                                        .orElseThrow(() -> KOSException.of(ErrorCode.WEAPON_SET_CONFIG_IS_NOT_FOUND));
    }

    public List<WeaponSetConfig> getAllConfigModel() {
        return weaponSetConfigRepository.findAll();
    }

}
