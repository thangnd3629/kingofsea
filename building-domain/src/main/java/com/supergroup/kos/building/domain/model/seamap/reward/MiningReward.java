package com.supergroup.kos.building.domain.model.seamap.reward;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@DiscriminatorValue("MINING")
@Getter
@Setter
@Accessors(chain = true)
public class MiningReward extends SeaReward {
    @Column(columnDefinition = "int default 0")
    private Double stone = 0D;
    @Column(columnDefinition = "int default 0")
    private Double wood = 0D;
}
