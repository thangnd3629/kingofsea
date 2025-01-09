package com.supergroup.kos.building.domain.model.technology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.common.base.Strings;
import com.supergroup.core.model.BaseModel;
import com.supergroup.core.utils.StringUtil;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.TechnologyCode;
import com.supergroup.kos.building.domain.constant.TechnologyType;
import com.supergroup.kos.building.domain.constant.research.TargetType;
import com.supergroup.kos.building.domain.constant.research.UnitType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_technology", indexes = @Index(columnList = "name, technologyType, code"))
@Getter
@Setter
@Accessors(chain = true)
public class Technology extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String name;
    private String description;
    private Long   techPoint;
    private Long   levelBuilding;

    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private TechnologyCode code;

    @Enumerated(EnumType.STRING)
    private TechnologyType technologyType;

    // use for unlock bulding item
    @Transient
    private List<BuildingName> unLockListBuildingName;
    @Basic
    @Column(name = "unlock_list_building_name")
    private String             unlockListBuildingNameValue;

    // use for unlock max slot relic
    private Long maxListingRelic;

    // use for reduce upgrading time percent techonology
    private Double reduceUpgradingTimePercent;

    // use for clear cutting
    private Double bonusWoodProductionPercent;

    // use for stone grinder
    private Double bonusStoneProductionPercent;

    // use for domestic growth
    private Double bonusGoldProductionPercent;

    private Double bonusCapGoldStoragePercent;

    private Double bonusCapStoneStoragePercent;

    private Double bonusCapWoodStoragePercent;

    private Double bonusEffectRelicItemPercent;

    // use for discount duration
    private Double discountPercentDurationTime;

    // use for discount rss
    private Double discountPercentRss;

    // use for unlock max Slot Weapon Mother Ship
    private Long maxSlotWeaponOfMotherShip;

    // use for upgradable level escort ship
    private Long maxLevelEscortShip;

    // use for technology bonus protect resource (SC21)
    private Double bonusProtectResourcePercent;

    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    @Enumerated(EnumType.STRING)
    private UnitType unitType;

    // use to describe technology tree
    @ManyToMany
    @JoinTable(name = "tbl_technology_technologies",
               joinColumns = @JoinColumn(name = "technology_1_id"),
               inverseJoinColumns = @JoinColumn(name = "technologies_2_id"))
    private Collection<Technology> conditions = new ArrayList<>();

    @PostLoad
    void postLoad() {
        if (Objects.nonNull(unlockListBuildingNameValue) && !Strings.isNullOrEmpty(unlockListBuildingNameValue)) {
            unLockListBuildingName = StringUtil.getListStringFromRawStringComma(unlockListBuildingNameValue)
                                               .stream()
                                               .map(BuildingName::valueOf).collect(Collectors.toList());
        }
    }

}
