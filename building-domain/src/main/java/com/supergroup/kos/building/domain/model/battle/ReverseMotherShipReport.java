package com.supergroup.kos.building.domain.model.battle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.supergroup.kos.building.domain.constant.MotherShipQualityKey;
import com.supergroup.kos.building.domain.constant.battle.FactionType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_reserve_mother_ship_report")
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ReverseMotherShipReport extends ShipReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long                 id;
    @ManyToOne
    @JoinColumn(name = "battle_profile_id")
    private BattleProfile        battleProfile;
    @Enumerated(EnumType.STRING)
    private MotherShipQualityKey quality;

    public ReverseMotherShipReport(FactionType faction) {
        super(faction);
    }
}
