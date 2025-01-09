package com.supergroup.kos.building.domain.model.seamap;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Embeddable
@NoArgsConstructor
public class InfoElement {
    private String islandName;
    @Embedded
    Coordinates coordinates;
}
