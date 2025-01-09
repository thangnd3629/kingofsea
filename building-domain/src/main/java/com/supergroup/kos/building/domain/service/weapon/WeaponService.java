package com.supergroup.kos.building.domain.service.weapon;

import java.util.List;

import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetWeaponByIdCommand;
import com.supergroup.kos.building.domain.command.GetWeaponsCommand;
import com.supergroup.kos.building.domain.model.config.WeaponConfig;
import com.supergroup.kos.building.domain.model.weapon.Weapon;
import com.supergroup.kos.building.domain.repository.persistence.weapon.WeaponConfigRepository;
import com.supergroup.kos.building.domain.repository.persistence.weapon.WeaponRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Service
@RequiredArgsConstructor
public class WeaponService {

    @Delegate
    private final WeaponRepository weaponRepository;
    private final WeaponConfigRepository weaponConfigRepository;

    public List<Weapon> getWeapons(GetWeaponsCommand command) {
        return weaponRepository.findByKosProfileId(command.getKosProfileId());
    }

    public List<Weapon> getWeaponNotEquipped(GetWeaponsCommand command) {
        return weaponRepository.findByKosProfileIdAndMotherShipNull(command.getKosProfileId());
    }

    public Weapon getWeaponById(GetWeaponByIdCommand command) {
        return weaponRepository.findByKosProfileIdAndWeaponId(command.getKosProfileId(), command.getWeaponId())
                               .orElseThrow(() -> KOSException.of(ErrorCode.WEAPON_IS_NOT_FOUND));
    }

    public List<Weapon> getByWeaponConfigIdAndKosProfileId(Long weaponConfigId, Long kosProfileId) {
        return weaponRepository.findByWeaponConfigIdAndKosProfileId(weaponConfigId, kosProfileId);
    }

    public Weapon save(Weapon weapon) {
        return weaponRepository.save(weapon);
    }

    public List<Weapon> saveAll(List<Weapon> weapons) {
        return weaponRepository.saveAll(weapons);
    }
    public List<WeaponConfig> getAllConfig(){
        return weaponConfigRepository.findAll();
    }

}
