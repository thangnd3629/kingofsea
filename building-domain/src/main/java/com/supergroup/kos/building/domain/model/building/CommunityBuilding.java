package com.supergroup.kos.building.domain.model.building;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.kos.building.domain.model.relic.Relic;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_community_building")
@Getter
@Setter
@Accessors(chain = true)
public class CommunityBuilding extends BaseBuilding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long              id;
    private Long              maxListingRelic;
    @OneToMany(mappedBy = "communityBuilding")
    private Collection<Relic> ownRelics = new java.util.ArrayList<>();

    @Transient
    private Long mpGained;
    @Transient
    private Long maxLevelListingRelic;

}
