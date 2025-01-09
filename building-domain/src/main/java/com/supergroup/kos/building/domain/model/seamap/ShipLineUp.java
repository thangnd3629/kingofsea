package com.supergroup.kos.building.domain.model.seamap;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.battle.FactionType;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;
import com.supergroup.kos.building.domain.model.battle.BossSeaEmbedded;
import com.supergroup.kos.building.domain.model.ship.MotherShip;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_ship_line_up")
@Getter
@Setter
@Accessors(chain = true)
public class ShipLineUp extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long       id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_mother_ship_id")
    private MotherShip activeMotherShip;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mother_ship_id")
    private MotherShip motherShip;

    @OneToMany(mappedBy = "lineUp", cascade = CascadeType.PERSIST)
    private List<EscortShipSquad> escortShipSquad;
    @OneToOne(mappedBy = "lineUp")
    private SeaActivity           activity;
    private Long                  escortShipUnits;

    private LocalDateTime timeJoinedBattle;
    @Transient
    private FactionType   factionType;

    @ManyToOne
    @JoinColumn(name = "battle_profile_id")
    private BattleProfile battleProfile;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "boss_id")),
            @AttributeOverride(name = "configId", column = @Column(name = "boss_config_id")),
    })
    private BossSeaEmbedded bossSea;

}
