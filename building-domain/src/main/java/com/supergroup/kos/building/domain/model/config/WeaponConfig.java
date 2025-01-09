package com.supergroup.kos.building.domain.model.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.supergroup.core.constant.BaseStatus;
import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.WeaponStat;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_weapon_config")
@Getter
@Setter
@Accessors(chain = true)
public class WeaponConfig extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long       id;
    private String     name;
    private String     thumbnail;
    private String     description;
    private Long       stat;
    @Enumerated(EnumType.STRING)
    @Column(name = "statistic_type")
    private WeaponStat stat_type;
    private BaseStatus status;
}
