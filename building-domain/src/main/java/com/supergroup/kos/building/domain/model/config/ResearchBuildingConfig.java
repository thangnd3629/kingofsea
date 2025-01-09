package com.supergroup.kos.building.domain.model.config;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.supergroup.kos.building.domain.model.technology.Technology;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_research_building_config")
@Getter
@Setter
@Accessors(chain = true)
public class ResearchBuildingConfig extends BaseBuildingConfig {

    private Double convertRate;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "research_building_config_id")
    private Collection<Technology> unlockTechnologies = new ArrayList<>();

}
