package com.supergroup.kos.building.domain.service.seamap;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetMotherShipCommand;
import com.supergroup.kos.building.domain.constant.WeaponStat;
import com.supergroup.kos.building.domain.constant.battle.ShipStatisticType;
import com.supergroup.kos.building.domain.model.config.EscortShipConfig;
import com.supergroup.kos.building.domain.model.config.MotherShipConfig;
import com.supergroup.kos.building.domain.model.seamap.KosWarInfo;
import com.supergroup.kos.building.domain.model.ship.EscortShip;
import com.supergroup.kos.building.domain.model.ship.MotherShip;
import com.supergroup.kos.building.domain.model.weapon.Weapon;
import com.supergroup.kos.building.domain.model.weapon.WeaponSet;
import com.supergroup.kos.building.domain.repository.persistence.seamap.KosWarInfoRepository;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipLevelConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipRepository;
import com.supergroup.kos.building.domain.service.ship.MotherShipService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KosWarInfoService {
    private final KosWarInfoRepository            kosWarInfoRepository;
    private final MotherShipService               motherShipService;
    private final EscortShipRepository            escortShipRepository;
    private final EscortShipLevelConfigDataSource escortShipLevelConfigDataSource;

    public KosWarInfo save(KosWarInfo kosWarInfo) {
        return kosWarInfoRepository.save(kosWarInfo);
    }

    public void saveAll(List<KosWarInfo> kosWarInfo) {
        kosWarInfoRepository.saveAll(kosWarInfo);
    }

    public KosWarInfo getByKosProfileId(Long kosProfileId) {
        Optional<KosWarInfo> nullable = kosWarInfoRepository.findByKosProfileId(kosProfileId);
        if (nullable.isEmpty()) {throw new KOSException(ErrorCode.WAR_INFO_NOT_FOUND);}
        return nullable.get();
    }

    public Double getTotalPower(Long kosProfileId) {
        try {
            List<MotherShip> motherShips = motherShipService.getMotherShips(new GetMotherShipCommand().setKosProfileId(kosProfileId));
            List<EscortShip> escortShips = escortShipRepository.findByKosProfileId(kosProfileId);
            double power = 0L;
            power += getMotherShipsPower(motherShips, ShipStatisticType.ATK1) + getEscortsShipPower(escortShips, ShipStatisticType.ATK1)
                     + getMotherShipsPower(motherShips, ShipStatisticType.ATK2) + getEscortsShipPower(escortShips, ShipStatisticType.ATK2);
            return power;
        } catch (KOSException e) {
            if (e.getCode().equals(ErrorCode.BUILDING_IS_LOCKED)) {
                return 0.0D;
            } else {
                throw e;
            }
        }
    }

    public static Double getMotherShipPower(MotherShip motherShip, ShipStatisticType type) {
        double totalPower = 0D;
        double motherShipPower = 0D;
        Double statBoostByLevel = motherShip.getMotherShipLevelConfig().getPercentStat();
        Double statBoostByQuality = motherShip.getMotherShipConfigQualityConfig().getMotherShipQualityConfig().getPercentStat();
        MotherShipConfig config = motherShip.getMotherShipConfigQualityConfig().getMotherShipConfig();
        WeaponStat weaponStat;
        switch (type) {
            case ATK1:
                motherShipPower = config.getAtk1();
                weaponStat = WeaponStat.ATK1;
                break;
            case ATK2:
                motherShipPower = config.getAtk2();
                weaponStat = WeaponStat.ATK2;
                break;
            case DEF1:
                motherShipPower = config.getDef1();
                weaponStat = WeaponStat.DEF1;
                break;
            case DEF2:
                motherShipPower = config.getDef2();
                weaponStat = WeaponStat.DEF2;
                break;
            case HP:
                motherShipPower = config.getHp();
                weaponStat = WeaponStat.HP;
                break;
            case DODGE:
                motherShipPower = config.getDodge();
                weaponStat = WeaponStat.DODGE;
                break;
            case TONNAGE:
                motherShipPower = config.getTng();
                weaponStat = WeaponStat.TNG;
                break;
            case CMD:
                motherShipPower = config.getCmd();
                weaponStat = WeaponStat.CMD;
                break;
            case SPEED:
                motherShipPower = config.getSpeed();
                weaponStat = WeaponStat.SPEED;
                break;
            default:
                return 0D;
        }
        totalPower += motherShipPower * statBoostByLevel * statBoostByQuality;
        Collection<Weapon> weapons = motherShip.getWeapons();
        Collection<WeaponSet> weaponSets = motherShip.getWeaponSets();
        totalPower += getWeaponPower(weapons, weaponStat) + getWeaponSetPower(weaponSets, weaponStat);
        return totalPower;
    }

    public Double getSingleEscortShipPower(EscortShip escortShip, ShipStatisticType type) {
        double totalPower = 0D;
        double escortShipPower = 0D;
        WeaponStat weaponStat;
        Double boostStatByGroup = escortShip.getEscortShipGroup().getEscortShipGroupLevelConfig().getPercentStat();
        var escortShipLevelConfig = escortShipLevelConfigDataSource.getByTypeAndLevel(escortShip.getEscortShipConfig().getType(),
                                                                                      escortShip.getLevel());
        Double boostStatByLevel = escortShipLevelConfig.getPercentStat();
        EscortShipConfig config = escortShip.getEscortShipConfig();
        switch (type) {
            case ATK1:
                escortShipPower = config.getAtk1();
                break;
            case ATK2:
                escortShipPower = config.getAtk2();
                break;
            case DEF1:
                escortShipPower = config.getDef1();
                break;
            case DEF2:
                escortShipPower = config.getDef2();
                break;
            case HP:
                escortShipPower = config.getHp();
                break;
            case DODGE:
                escortShipPower = config.getDodge();
                break;
            default:
                return 0D;
        }
        return
                (escortShipPower) * boostStatByLevel * boostStatByGroup;
    }

    public static Double getWeaponPower(Collection<Weapon> weapons, WeaponStat type) {
        double power = 0L;
        if (Objects.nonNull(weapons)) {
            for (Weapon weapon : weapons) {
                WeaponStat statType = weapon.getWeaponConfig().getStat_type();
                if (Objects.nonNull(statType) && statType.equals(type)) {
                    power += weapon.getWeaponConfig().getStat();
                }
            }
        }
        return power;
    }

    public static Double getWeaponSetPower(Collection<WeaponSet> weaponSets, WeaponStat type) {
        double power = 0L;
        if (Objects.nonNull(weaponSets)) {
            for (WeaponSet weaponSet : weaponSets) {
                WeaponStat statType = weaponSet.getWeaponSetConfig().getStat_type();
                if (Objects.nonNull(statType) && statType.equals(type)) {
                    power += weaponSet.getWeaponSetConfig().getStat() * weaponSet.getWeaponSetLevelConfig().getPercentStat();
                }
            }
        }
        return power;
    }

    public Double getMotherShipsPower(Collection<MotherShip> motherShips, ShipStatisticType type) {
        double power = 0L;
        for (MotherShip motherShip : motherShips) {
            power += getMotherShipPower(motherShip, type);
        }
        return power;
    }

    public Double getEscortsShipPower(Collection<EscortShip> escortShips, ShipStatisticType type) {
        double power = 0L;
        for (EscortShip escortShip : escortShips) {
            power += getSingleEscortShipPower(escortShip, type) * escortShip.getAmount();
        }
        return power;
    }

    public List<KosWarInfo> findByKosProfile_IdIn(Set<Long> idsKosProfile) {
        return kosWarInfoRepository.findByKosProfile_IdIn(idsKosProfile);
    }
}