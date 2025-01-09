package com.supergroup.kos.building.domain.model.seamap;

import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.config.seamap.SeaElementConfig;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@Table(name = "tbl_element_sea", indexes = @Index(columnList = "x, y"))
@Getter
@Setter
@Accessors(chain = true)
@RedisHash("SeaElement")
public abstract class SeaElement extends BaseModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long        id;
    @Embedded
    private Coordinates coordinates;
    private Boolean     isRefreshable = false;
    private Boolean     deleted       = false;

    /**
     * We will separate map into same size piece
     * vd: This is EXAMPLE, NOT right logic
     * ==================================
     * ||       ||      ||      ||      ||
     * ||  1,1  ||  1,2 ||  1,3 ||  1,4 ||
     * ==================================
     * ||       ||      ||      ||      ||
     * ||  2,1  ||  2,2 ||  2,3 ||  x,y ||
     * ==================================
     * This field must not be null. This is index key in redis database
     * Ox name for parcel
     */
    @Indexed
    @Transient
    private Integer parcelX;

    /**
     * This field must not be null. This is index key in redis database
     * Ox name for parcel
     */
    @Indexed
    @Transient
    private Integer parcelY;

    // This field must not be null. This is index key in redis database
    @Transient
    @Indexed
    private Long x;
    // This field must not be null. This is index key in redis database
    @Transient
    @Indexed
    private Long y;

    /**
     * flag active island
     */
    private Boolean active = false;

    /**
     * flag refresh state and location
     */
    private Long dependentElementId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "elements_config_id")
    private SeaElementConfig seaElementConfig;

    /**
     * The Battle is happening on sea element
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "battle_id")
    private Battle battle;

    /**
     * Sea element is a station
     * Sea activities is mooring at sea element
     */
    @OneToMany(mappedBy = "stationAt", fetch = FetchType.LAZY)
    private List<SeaActivity> seaActivities;

    /**
     * Current invader this base
     */
    @Embedded
    private Invader invader;

    public SeaElement setCoordinate(Coordinates coordinates) {
        this.coordinates = coordinates;
        this.x = coordinates.getX();
        this.y = coordinates.getY();
        return this;
    }

    @PostLoad
    private void postLoad() {
        this.setCoordinate(this.getCoordinates());
    }

    public abstract SeaElementType type();

    public Boolean isOccupied() {
        return Objects.nonNull(invader)
               && Objects.nonNull(invader.getKosProfileInvader())
               && Objects.nonNull(invader.getKosProfileInvader().getId());
    }

    public abstract String name();
}
