package com.supergroup.kos.building.domain.model.battle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.supergroup.kos.building.domain.constant.EscortShipGroupName;
import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.building.domain.constant.battle.FactionType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_reserve_escort_ship_report")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ReverseEscortShipReport extends ShipReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long           id;
    @Enumerated(EnumType.STRING)
    private EscortShipType shipType;
    @Column(name = "col_left")
    private Long           left; // rename reverse to do

    @Enumerated(EnumType.STRING)
    private EscortShipType      escortShipType;
    @Enumerated(EnumType.STRING)
    private EscortShipGroupName escortShipGroupName;

    public ReverseEscortShipReport(FactionType faction) {
        super(faction);
    }
}
