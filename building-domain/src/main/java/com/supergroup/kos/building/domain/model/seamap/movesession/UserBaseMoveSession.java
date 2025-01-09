package com.supergroup.kos.building.domain.model.seamap.movesession;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType.Constants;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@DiscriminatorValue(value = Constants.USER_BASE)
@Accessors(chain = true)
@Getter
@Setter
public class UserBaseMoveSession extends MoveSession {
    @Override
    public SeaElementType getDestinationType() {
        return SeaElementType.USER_BASE;
    }
}
