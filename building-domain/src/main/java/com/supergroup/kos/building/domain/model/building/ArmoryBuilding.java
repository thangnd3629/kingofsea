package com.supergroup.kos.building.domain.model.building;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.kos.building.domain.model.ship.EscortShipGroup;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_armory_building")
@Getter
@Setter
@Accessors(chain = true)
public class ArmoryBuilding extends BaseBuilding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long                 id;
    @Column(columnDefinition = "boolean default true")
    private Boolean              isLockMergeWeapon;

    @Transient
    private Long                        numberOfWeaponSet;
    @Transient
    private Collection<EscortShipGroup> escortShipGroups;
}
