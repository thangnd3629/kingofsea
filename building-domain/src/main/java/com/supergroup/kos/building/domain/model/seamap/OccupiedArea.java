package com.supergroup.kos.building.domain.model.seamap;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Embeddable
public class OccupiedArea implements Serializable {
    private Long width; // Ox
    private Long height; // Oy
    private Long length; // Oz
}
