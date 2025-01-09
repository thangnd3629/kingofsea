package com.supergroup.kos.building.domain.model.config.seamap;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.supergroup.kos.building.domain.constant.seamap.ResourceIslandType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@DiscriminatorValue("RESOURCE")
@Getter
@Setter
@Accessors(chain = true)
public class ResourceIslandConfig extends SeaElementConfig {
    @Column(name = "resource_type")
    @Enumerated(EnumType.STRING)
    private ResourceIslandType resourceType;
    @Column(name = "resource_capacity")
    private Double               resourceCapacity;
    @Column(name = "resource_exploit_speed")
    private Double             resourceExploitSpeed;

}
