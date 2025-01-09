package com.supergroup.kos.building.domain.model.ship;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.config.EscortShipGroupLevelConfig;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_escort_ship_group")
@Getter
@Setter
@Accessors(chain = true)
public class EscortShipGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long                       id;

    @ManyToOne
    @JoinColumn(name = "assets_id")
    private Assets                     assets;
    @ManyToOne
    @JoinColumn(name = "escort_ship_group_level_config_id")
    private EscortShipGroupLevelConfig escortShipGroupLevelConfig;
    @OneToMany(mappedBy = "escortShipGroup")
    private Collection<EscortShip>     escortShips;

}
