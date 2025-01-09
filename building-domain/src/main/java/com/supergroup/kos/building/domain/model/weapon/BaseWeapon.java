package com.supergroup.kos.building.domain.model.weapon;

import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.supergroup.kos.building.domain.model.ship.MotherShip;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@MappedSuperclass
@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class BaseWeapon {
    @ManyToOne
    @JoinColumn(name = "mother_ship_id")
    private MotherShip motherShip;
}
