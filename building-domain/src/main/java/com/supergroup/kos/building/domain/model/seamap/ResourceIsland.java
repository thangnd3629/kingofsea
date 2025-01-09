package com.supergroup.kos.building.domain.model.seamap;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.supergroup.kos.building.domain.constant.seamap.ResourceIslandType;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@DiscriminatorValue("RESOURCE")
@Getter
@Setter
@Accessors(chain = true)
public class ResourceIsland extends SeaElement {
    @Column(columnDefinition = "float8 default 0")
    private Double             mined;
    @OneToOne
    @JoinColumn(name = "mining_session_id")
    private SeaMiningSession   miningSession;
    @Enumerated(EnumType.STRING)
    private ResourceIslandType resourceType;

    @Override
    public SeaElementType type() {
        return SeaElementType.RESOURCE;
    }

    @Override
    public String name() {
        return getSeaElementConfig().getName();
    }

}
