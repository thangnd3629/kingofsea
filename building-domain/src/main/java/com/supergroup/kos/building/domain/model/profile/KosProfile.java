package com.supergroup.kos.building.domain.model.profile;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.auth.domain.model.User;
import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.building.CastleBuilding;
import com.supergroup.kos.building.domain.model.building.CommunityBuilding;
import com.supergroup.kos.building.domain.model.building.VaultBuilding;
import com.supergroup.kos.building.domain.model.mining.QueenBuilding;
import com.supergroup.kos.building.domain.model.point.Point;
import com.supergroup.kos.building.domain.model.seamap.UserBase;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_kos_profile")
@Getter
@Setter
@Accessors(chain = true)
public class KosProfile extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = javax.persistence.FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "kosProfile", cascade = CascadeType.PERSIST)
    private Assets assets;

    @OneToOne(mappedBy = "kosProfile")
    private Point point;

    @Transient
    private Long level;

    @OneToOne(mappedBy = "kosProfile")
    private UserBase base;

    @OneToOne(mappedBy = "kosProfile")
    private CommunityBuilding communityBuilding;

    @OneToOne(mappedBy = "kosProfile")
    private VaultBuilding vaultBuilding;

    @OneToOne(mappedBy = "kosProfile")
    private QueenBuilding queenBuilding;

    @OneToOne(mappedBy = "kosProfile")
    private CastleBuilding castleBuilding;

    // Ability
    private Boolean isUnlockMilitaryTech         = false;
    private Boolean canUseSpeedItem              = false;
    private Boolean isUnlockAdvancedMilitaryTech = false;

    // Helpful effect
    private Double reduceUpgradingTimePercent  = 0D;
    private Double bonusWoodProductionPercent  = 0D;
    private Double bonusStoneProductionPercent = 0D;
    private Double bonusGoldProductionPercent  = 0D;
    private Double bonusCapGoldStoragePercent  = 0D;
    private Double bonusCapStoneStoragePercent = 0D;
    private Double bonusCapWoodStoragePercent  = 0D;
    private Double bonusEffectRelicItemPercent = 0D;
    private Double bonusProtectResourcePercent = 0D;

    public void addReduceUpgradingTimePercent(Double percent) {
        reduceUpgradingTimePercent += percent * (1 - reduceUpgradingTimePercent);
    }

    public void addBonusWoodProductionPercent(Double percent) {
        bonusWoodProductionPercent += percent * (1 + bonusWoodProductionPercent);
    }

    public void addBonusStoneProductionPercent(Double percent) {
        bonusStoneProductionPercent += percent * (1 + bonusStoneProductionPercent);
    }

    public void addBonusGoldProductionPercent(Double percent) {
        bonusGoldProductionPercent += percent * (1 + bonusGoldProductionPercent);
    }

    public void addBonusCapGoldStoragePercent(Double percent) {
        bonusCapGoldStoragePercent += percent * (1 + bonusCapGoldStoragePercent);
    }

    public void addBonusCapStoneStoragePercent(Double percent) {
        bonusCapStoneStoragePercent += percent * (1 + bonusCapStoneStoragePercent);
    }

    public void addBonusCapWoodStoragePercent(Double percent) {
        bonusCapWoodStoragePercent += percent * (1 + bonusCapWoodStoragePercent);
    }

    public void addBonusEffectRelicItemPercent(Double percent) {
        bonusEffectRelicItemPercent += percent * (1 + bonusEffectRelicItemPercent);
    }

    public void addBonusProtectResourcePercent(Double percent) {
        bonusProtectResourcePercent += percent * (1 + bonusProtectResourcePercent);
    }
}
