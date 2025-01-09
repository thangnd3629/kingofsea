package com.supergroup.kos.building.domain.model.profile;

import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class KosProfileLocation {
    private Long x;
    private Long y;
}
