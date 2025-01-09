package com.supergroup.kos.building.domain.model.scout;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.MissionResult;
import com.supergroup.kos.building.domain.constant.MissionStatus;
import com.supergroup.kos.building.domain.constant.MissionType;
import com.supergroup.kos.building.domain.constant.ScoutMode;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_scout")
@Getter
@Setter
@Accessors(chain = true)
public class Scout extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private LocalDateTime timeStart;
    private Long          missionTime; // scouting time, including departure and return (seconds)
    @Enumerated(EnumType.STRING)
    private MissionType   missionType;
    @Enumerated(EnumType.STRING)
    private MissionStatus missionStatus;
    @Enumerated(EnumType.STRING)
    private ScoutMode     scoutMode;
    @Enumerated(EnumType.STRING)
    private MissionResult result;
    private Long          numberArmy;
    private Long          numberEnemy;
    private Long          soliderDie = 0L;
    private Double        speed;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "targetX")),
            @AttributeOverride(name = "y", column = @Column(name = "targetY"))
    })
    private Coordinates target;

    @ManyToOne
    @JoinColumn(name = "scouter_id", nullable = false)
    private KosProfile scouter;

    @OneToMany(mappedBy = "scout", orphanRemoval = true)
    private List<ScoutReport> scoutReports = new ArrayList<>();


    @Transient
    private SeaElement seaElement;
    @Transient
    private KosProfile kosProfileTarget;


    public Long getSoliderRemain() {
        if(Objects.nonNull(numberArmy)) {
            return Math.max(0L, numberArmy - soliderDie);
        }
        return 0L;
    }

}
