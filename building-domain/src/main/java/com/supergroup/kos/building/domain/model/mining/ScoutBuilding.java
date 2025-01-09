package com.supergroup.kos.building.domain.model.mining;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.kos.building.domain.model.building.BaseBuilding;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_scout_building")
@Getter
@Setter
@Accessors(chain = true)
public class ScoutBuilding extends BaseBuilding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private Long          totalScout;
    private Long          availableScout;
    private Boolean       isTraining = false;
    private LocalDateTime startTrainingTime;
    private Long          trainingDuration;
    private Long          scoutTraining;

    // anti scout item
    private Boolean canScout;

    // blind scout item
    private Boolean isBlindScout = false;
    private Double  blindMulti   = 1D;

    // use for unlock feature scout
    @Column(columnDefinition = "boolean default false")
    private Boolean unlockScoutFeature;

    @Transient
    private Long capacity;
    @Transient
    private Long numberMission;

    public Boolean canScout() {
        return Objects.isNull(canScout) || canScout;
    }

    public Boolean isBlindScout() {
        return Objects.nonNull(isBlindScout) && isBlindScout;
    }

    public Double blindMulti() {
        return Objects.isNull(blindMulti) ? 1D : blindMulti;
    }
}
