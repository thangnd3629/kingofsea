package com.supergroup.kos.building.domain.model.config;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.core.constant.BaseStatus;
import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.TechnologyCode;
import com.supergroup.kos.building.domain.constant.WeaponStat;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_weapon_set_config")
@Getter
@Setter
@Accessors(chain = true)
public class WeaponSetConfig extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long       id;
    private String     name;
    private String     thumbnail;
    private String     description;
    private Long       gold;
    private Double     percentSuccess;
    private Long       stat;
    private BaseStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "statistic_type")
    private WeaponStat     stat_type;
    @Enumerated(EnumType.STRING)
    private TechnologyCode technologyRequirement;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tbl_weapon_set_weapon_config",
               joinColumns = @JoinColumn(name = "weapon_set_config_id"),
               inverseJoinColumns = @JoinColumn(name = "weapon_config_id"))
    private Collection<WeaponConfig> weaponConfigs = new ArrayList<>();

    @Transient
    private Long qualityExist;

}
