package com.supergroup.kos.building.domain.model.mining;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.supergroup.kos.building.domain.model.building.BaseBuilding;
import com.supergroup.kos.building.domain.model.technology.UserTechnology;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author idev
 * <p>
 * This class is used in infrastructure
 */
@Entity
@Table(name = "tbl_research_building")
@Getter
@Setter
@Accessors(chain = true)
public class ResearchBuilding extends BaseBuilding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "researchBuilding", cascade = javax.persistence.CascadeType.PERSIST, orphanRemoval = true)
    private Collection<UserTechnology> userTechnologies = new ArrayList<>();

}
