package com.supergroup.kos.building.domain.model.seamap;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;

@Entity
@DiscriminatorValue("ANCHOR")
public class Anchor extends SeaElement {
    @Override
    public SeaElementType type() {
        return null;
    }

    @Override
    public String name() {
        return "Anchor"; // TODO this is hard code
    }
}
