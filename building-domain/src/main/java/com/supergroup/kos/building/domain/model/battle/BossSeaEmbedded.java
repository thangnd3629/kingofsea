package com.supergroup.kos.building.domain.model.battle;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class BossSeaEmbedded {
    private Long id;
    private Long configId;
}
