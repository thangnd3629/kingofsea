package com.supergroup.kos.building.domain.model.queen;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.model.config.QueenConfig;
import com.supergroup.kos.building.domain.model.mining.QueenBuilding;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_queen")
@Getter
@Setter
@Accessors(chain = true)
public class Queen extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long          id;
    @ManyToOne
    @JoinColumn(name = "queen_config_id")
    private QueenConfig   queenConfig;
    @ManyToOne
    @JoinColumn(name = "queen_building_id")
    private QueenBuilding queenBuilding;
}
