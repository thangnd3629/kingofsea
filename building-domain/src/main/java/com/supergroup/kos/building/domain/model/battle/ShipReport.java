package com.supergroup.kos.building.domain.model.battle;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.battle.FactionType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@MappedSuperclass
@Accessors(chain = true)
@NoArgsConstructor
public abstract class ShipReport extends BaseModel {
    @Enumerated(EnumType.STRING)
    private FactionType faction;
    private Long        modelId; // bad code because ship model id is not abstract
    @ManyToOne
    @JoinColumn(name = "round_report_id")
    private RoundReport roundReport;

    public ShipReport(FactionType factionType) {
        this.faction = factionType;
    }
}
