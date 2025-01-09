package com.supergroup.kos.building.domain.model.seamap;

import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.supergroup.kos.building.domain.constant.seamap.MoveSessionType;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.model.profile.KosProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@DiscriminatorValue("SHIP")
@Getter
@Setter
@Accessors(chain = true)
public class ShipElement extends SeaElement {

    private Double        speed;
    private LocalDateTime startTime;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "startX")),
            @AttributeOverride(name = "y", column = @Column(name = "startY"))
    })
    private Coordinates start;

    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "endX")),
            @AttributeOverride(name = "y", column = @Column(name = "endY"))
    })
    @Embedded
    private Coordinates end;

    @Enumerated(EnumType.STRING)
    private MoveSessionType shipStatus; // mission of this ship. Ex: SCOUT, ATTACk

    @ManyToOne
    @JoinColumn(name = "kos_profile_id")
    private KosProfile kosProfile; // owner of the ship

    @Override
    public SeaElementType type() {
        return SeaElementType.SHIP;
    }

    @Override
    public String name() {
        return "Ship " + kosProfile.getUser().getUserProfile().getUsername();
    }
}
