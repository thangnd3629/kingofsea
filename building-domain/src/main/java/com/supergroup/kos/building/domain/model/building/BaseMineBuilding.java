package com.supergroup.kos.building.domain.model.building;

import java.time.LocalDateTime;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@MappedSuperclass
@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
public class BaseMineBuilding extends BaseBuilding {
    private Long          worker;
    private LocalDateTime lastTimeClaim;

    @Transient
    private Double production;
    @Transient
    private Double currentSpeedPerWorker;
}
