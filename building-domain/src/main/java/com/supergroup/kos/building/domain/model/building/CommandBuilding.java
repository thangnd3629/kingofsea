package com.supergroup.kos.building.domain.model.building;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.kos.building.domain.model.ship.MotherShip;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_command_building")
@Getter
@Setter
@Accessors(chain = true)
public class CommandBuilding extends BaseBuilding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long    id;
    private Long    maxSlotWeaponOfMotherShip;
    private Boolean isLockBuyMother = true;

    @OneToMany(mappedBy = "commandBuilding")
    private Collection<MotherShip> motherShips;

    @Transient
    private Long slotMotherShip;

}
