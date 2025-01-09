package com.supergroup.kos.building.domain.model.relic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.supergroup.core.constant.BaseStatus;
import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.model.building.CommunityBuilding;
import com.supergroup.kos.building.domain.model.config.RelicConfig;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_relic")
@Getter
@Setter
@Accessors(chain = true)
public class Relic extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long              id;
    @Column(columnDefinition = "boolean default false")
    private Boolean           isListing;
    @ManyToOne
    @JoinColumn(name = "relic_config_id")
    private RelicConfig       relicConfig;
    @ManyToOne
    @JoinColumn(name = "community_building_id")
    private CommunityBuilding communityBuilding;

    private BaseStatus status;
}
