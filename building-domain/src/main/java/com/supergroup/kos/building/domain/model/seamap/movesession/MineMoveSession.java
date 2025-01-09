package com.supergroup.kos.building.domain.model.seamap.movesession;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType.Constants;

import lombok.experimental.Accessors;

@Entity
@DiscriminatorValue(value = Constants.RESOURCE)
@Accessors(chain = true)
public class MineMoveSession extends MoveSession {
    @Override
    public SeaElementType getDestinationType() {
        return SeaElementType.RESOURCE;
    }
}
