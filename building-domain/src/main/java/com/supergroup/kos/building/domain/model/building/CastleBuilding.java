package com.supergroup.kos.building.domain.model.building;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.core.utils.RoundUtil;
import com.supergroup.kos.building.domain.constant.IslandStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_castle_building")
@Getter
@Setter
@Accessors(chain = true)
public class CastleBuilding extends BaseBuilding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long         id;
    private Double       idlePeople   = 0D; // people idle
    private IslandStatus  islandStatus = IslandStatus.PEACE;
    private LocalDateTime lastTimeClaim;

    @Transient
    private Double peopleProduction;

    @Transient
    private Double goldProduction;

    @Transient
    private Long maxPopulation;

    @Transient
    private Double mpMultiplier;

    public CastleBuilding setIdlePeople(Double people) {
        this.idlePeople = RoundUtil.roundDouble(people);
        return this;
    }

}
