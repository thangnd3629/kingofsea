package com.supergroup.kos.building.domain.model.seamap;

import java.time.LocalDateTime;

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.seamap.SeaActivityStatus;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.scout.Scout;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;
import com.supergroup.kos.building.domain.model.seamap.reward.LoadedOnShipReward;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_activity_sea")
@Getter
@Setter
@Accessors(chain = true)
public class SeaActivity extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Embedded
    private Coordinates currentLocation;
    @ManyToOne
    @JoinColumn(name = "kos_profile_id")
    private KosProfile  kosProfile;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_move_session_id")
    private MoveSession activeMoveSession;

    /**
     * This tag for soft delete
     */
    private Boolean isDeleted;
    private Double  speed;

    @OneToOne
    @JoinColumn(name = "line_up_id")
    private ShipLineUp lineUp;

    /**
     * This is presentation for activity on the sea map
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ship_element_id")
    private ShipElement shipElement;

    /**
     * Scout from scout mission
     */

    @OneToOne
    @JoinColumn(name = "scout_id")
    private Scout scout;

    /**
     * Resource is on the ship
     * This can be reward from battle, mining,...
     * All reward will be loaded to here
     * It seem ship's storage
     */
    @OneToOne(mappedBy = "activity", cascade = CascadeType.PERSIST)
    private LoadedOnShipReward loadedOnShipReward;

    @Enumerated(EnumType.STRING)
    private SeaActivityStatus status;
    /**
     * time end for current action in this activity
     */
    private LocalDateTime     timeEnd;

    /**
     * This is current station
     * Null when it is moving
     */
    @ManyToOne
    @JoinColumn(name = "station_id")
    @Nullable
    private SeaElement stationAt;
}
