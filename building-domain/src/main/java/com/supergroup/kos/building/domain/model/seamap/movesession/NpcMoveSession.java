package com.supergroup.kos.building.domain.model.seamap.movesession;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType.Constants;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@DiscriminatorValue(value = Constants.NPC)
@Accessors(chain = true)
@Getter
@Setter
public class NpcMoveSession extends MoveSession {
    @Override
    public SeaElementType getDestinationType() {
        return SeaElementType.BOSS;
    }
}
