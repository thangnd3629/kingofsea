package com.supergroup.kos.building.domain.model.seamap;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.kos.building.domain.model.ship.EscortShip;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_escort_squad")
@Getter
@Setter
@Accessors(chain = true)

public class EscortShipSquad extends Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long       id;
    @ManyToOne
    @JoinColumn(name = "escort_ship_id")
    private EscortShip escortShip;
    private Long       initialAmount;
    private Long       amount = 0L;
    private Long       killed = 0L;
    private Long       hpLost = 0L;
    //    private Long       escortInBattle = 0L;
    @ManyToOne
    @JoinColumn(name = "line_up_id")
    private ShipLineUp lineUp;

    // battle
    @Transient
    private Long atk1;
    @Transient
    private Long atk2;
    @Transient
    private Long def1;
    @Transient
    private Long def2;
    @Transient
    private Long hp;
    @Transient
    private Long totalHp;

    public Long getRemain() {
        if (Objects.isNull(amount) || Objects.isNull(killed)) {
            return 0L;
        } else {
            return Math.max(this.amount - this.killed, 0L);
        }
    }
}
