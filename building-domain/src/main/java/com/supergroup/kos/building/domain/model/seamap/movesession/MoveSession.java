package com.supergroup.kos.building.domain.model.seamap.movesession;

import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_move_session")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "destination_type")
@Getter
@Setter
@Accessors(chain = true)
public abstract class MoveSession extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "startX")),
            @AttributeOverride(name = "y", column = @Column(name = "startY"))
    })
    private Coordinates start;
    private Long        sourceElementId;
    private Long        destinationElementId;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "endX")),
            @AttributeOverride(name = "y", column = @Column(name = "endY"))
    })
    private Coordinates end;

    private LocalDateTime withdrawnTime;
    private Double        speed;
    private LocalDateTime timeStart;
    private Double        duration;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sea_activity_id")
    private SeaActivity   seaActivity;
    private Boolean       isHidden;
    @Column(name = "isDeleted", columnDefinition = "boolean default false")
    private Boolean       isDeleted   = false;
    @Column(name = "isProcessed", columnDefinition = "boolean default false")
    private Boolean       isProcessed = false;
    @Enumerated(EnumType.STRING)
    private MissionType   missionType;
    private Long          kosTargetId;
    private Long          battleId;
    private Long          shipUnits;

    @Transient
    public abstract SeaElementType getDestinationType(); // destination type

    public Long getDurationInMillis() {
        return (long) (duration * 1000);
    }

}
