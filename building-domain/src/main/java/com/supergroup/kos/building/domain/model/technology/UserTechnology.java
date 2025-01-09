package com.supergroup.kos.building.domain.model.technology;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.model.mining.ResearchBuilding;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_user_technology")
@Getter
@Setter
@Accessors(chain = true)
public class UserTechnology extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "technology_id")
    private Technology technology;

    private Boolean isResearched = false;
    private Boolean isLock       = true;

    @ManyToOne
    @JoinColumn(name = "research_building_id")
    private ResearchBuilding researchBuilding;

}
