package com.supergroup.kos.building.domain.model.ship;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.kos.building.domain.constant.battle.ShipStatisticType;
import com.supergroup.kos.building.domain.constant.seamap.SeaActivityStatus;
import com.supergroup.kos.building.domain.model.building.CommandBuilding;
import com.supergroup.kos.building.domain.model.config.MotherShipConfigQualityConfig;
import com.supergroup.kos.building.domain.model.config.MotherShipLevelConfig;
import com.supergroup.kos.building.domain.model.seamap.Ship;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.model.technology.UserTechnology;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.model.weapon.Weapon;
import com.supergroup.kos.building.domain.model.weapon.WeaponSet;
import com.supergroup.kos.building.domain.service.seamap.KosWarInfoService;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_mother_ship")
@Getter
@Setter
@Accessors(chain = true)
@DynamicUpdate
public class MotherShip extends Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long          id;
    private Long          currentHp;
    private LocalDateTime arrivalMainBaseTime;
    private LocalDateTime lastTimeCalculateHp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeaActivityStatus status;

    @ManyToOne
    @JoinColumn(name = "upgrade_level_id")
    private UpgradeSession upgradeLevel;

    @ManyToOne
    @JoinColumn(name = "upgrade_quality_id")
    private UpgradeSession  upgradeQuality;
    @ManyToOne
    @JoinColumn(name = "command_building_id")
    private CommandBuilding commandBuilding;

    @ManyToOne
    @JoinColumn(name = "mother_ship_config_quality_config_id")
    private MotherShipConfigQualityConfig motherShipConfigQualityConfig;
    @ManyToOne
    @JoinColumn(name = "mother_ship_level_config_id")
    private MotherShipLevelConfig         motherShipLevelConfig;

    @OneToMany(mappedBy = "motherShip")
    private Collection<Weapon> weapons;

    @OneToMany(mappedBy = "motherShip")
    private Collection<WeaponSet> weaponSets;

    @OneToOne(mappedBy = "activeMotherShip")
    private ShipLineUp activeLineUp;

    @Transient
    private Long           maxSlotWeaponOfMotherShip;
    @Transient
    private UserTechnology technologyRequiredUnlockSlotWeapon;

    @Transactional
    public Boolean isHealing() {
        return status.equals(SeaActivityStatus.STANDBY) && currentHp.doubleValue() < getMaxHp();
    }

    /**
     * This method will re-calculate hp to get exact current hp
     */
    @Transactional
    public Long getCurrentHp() {
        validateCurrentHp();
        return Objects.nonNull(currentHp) ? currentHp : 0;
    }

    @Transactional
    public MotherShip setStatus(SeaActivityStatus status) {
        validateCurrentHp();
        this.status = status;
        return this;
    }

    /**
     * This method re-calculate (hp recovery,...)
     * Should call motherShipRepository.save() to save current hp to database
     */
    public void validateCurrentHp() {
        // re-calculate current hp
        var isUpdated = true;
        if (Objects.nonNull(arrivalMainBaseTime) && Objects.nonNull(lastTimeCalculateHp)) {
            isUpdated = calculateHpRecovery(lastTimeCalculateHp);
        } else if (Objects.nonNull(arrivalMainBaseTime)) {
            isUpdated = calculateHpRecovery(arrivalMainBaseTime);
        }
        if (isUpdated) {
            lastTimeCalculateHp = LocalDateTime.now();
        }
    }

    /**
     * Return true if updated
     */
    private Boolean calculateHpRecovery(LocalDateTime from) {

        // if mother ship is not stand by, cannot recovery hp
        if (!getStatus().equals(SeaActivityStatus.STANDBY)) {
            return false;
        }

        // check if full hp, ignore recovery
        if (currentHp >= getMaxHp()) {
            currentHp = getMaxHp().longValue();
            return false;
        }

        // recovery hp
        var timeDiff = ChronoUnit.SECONDS.between(from, LocalDateTime.now());
        var hpRecovery = motherShipConfigQualityConfig.getMotherShipConfig().getRecoverySpeed() * timeDiff;

        if (hpRecovery < 1.0) {
            return false;
        }

        currentHp += (long) hpRecovery;
        if (currentHp > getMaxHp()) {
            currentHp = Math.round(getMaxHp());
        }
        return true;
    }

    public Double getMaxHp() {
        return KosWarInfoService.getMotherShipPower(this, ShipStatisticType.HP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        MotherShip that = (MotherShip) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
