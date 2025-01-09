package com.supergroup.kos.building.domain.model.scout;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.MissionResult;
import com.supergroup.kos.building.domain.constant.MissionType;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.InfoElement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_scout_report")
@Getter
@Setter
@Accessors(chain = true)
public class ScoutReport extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private LocalDateTime timeStart;
    private Long          missionTime; //seconds
    private MissionType   missionType;
    private MissionResult result;
    private Long          numberEnemy;
    private Long          numberArmy;
    private Boolean       isSeen     = false;
    private Boolean       isBookmark = false;
    private Boolean       active     = false;

    @Embedded
    private InfoElement infoElementTarget; // when element update, still save the old info element

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "navigate_x")),
            @AttributeOverride(name = "y", column = @Column(name = "navigate_y"))
    })
    private Coordinates  navigate;

    @Transient
    private Double         speed;
    @Transient
    private ScoutingResult infoReceiveModel;

    @Basic
    @Column(name = "info_receive", columnDefinition = "TEXT")
    private String infoReceive;

    @ManyToOne
    @JoinColumn(name = "kos_profile_id", nullable = false)
    private KosProfile kosProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kos_profile_target_id")
    private KosProfile kosProfileTarget;

    @ManyToOne
    @JoinColumn(name = "scout_id")
    private Scout scout;

    @PostLoad
    void load() {
        Gson gson = new Gson();
        Type type = new TypeToken<ScoutingResult>() {}.getType();
        this.infoReceiveModel = gson.fromJson(this.infoReceive, type);
    }

    @PrePersist
    @PreUpdate
    void persist() {
        Gson gson = new Gson();
        this.infoReceive = gson.toJson(this.infoReceiveModel);
    }

}
