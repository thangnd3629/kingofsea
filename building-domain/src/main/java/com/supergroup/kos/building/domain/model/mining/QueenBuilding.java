package com.supergroup.kos.building.domain.model.mining;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.kos.building.domain.model.building.BaseBuilding;
import com.supergroup.kos.building.domain.model.queen.Queen;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_queen_building")
@Getter
@Setter
@Accessors(chain = true)
public class QueenBuilding extends BaseBuilding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long              id;
    private Long              numberOfQueenCard;
    @OneToMany(mappedBy = "queenBuilding", cascade = CascadeType.PERSIST)
    private Collection<Queen> ownQueens;

    @Transient
    private Long                    mpGained;

}
