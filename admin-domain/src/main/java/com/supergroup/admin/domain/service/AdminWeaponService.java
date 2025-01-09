package com.supergroup.admin.domain.service;

import org.springframework.stereotype.Service;

import com.supergroup.admin.domain.command.AdminCreateWeaponCommand;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.model.weapon.Weapon;
import com.supergroup.kos.building.domain.repository.persistence.weapon.WeaponConfigRepository;
import com.supergroup.kos.building.domain.repository.persistence.weapon.WeaponRepository;
import com.supergroup.kos.building.domain.service.asset.AssetsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminWeaponService {

    private final WeaponRepository       weaponRepository;
    private final AssetsService          assetsService;
    private final WeaponConfigRepository weaponConfigRepository;

    public Weapon createWeapon(AdminCreateWeaponCommand command) {
        var asset = assetsService.getAssets(new KosProfileCommand().setKosProfileId(command.getKosProfileId()));
        var model = weaponConfigRepository.findById(command.getModelId())
                                          .orElseThrow(() -> KOSException.of(ErrorCode.WEAPON_CONFIG_IS_NOT_FOUND));
        var weapon = new Weapon().setWeaponConfig(model).setAssets(asset);
        return weaponRepository.save(weapon);
    }
}
