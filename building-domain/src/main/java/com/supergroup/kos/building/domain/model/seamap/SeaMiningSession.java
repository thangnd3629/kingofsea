package com.supergroup.kos.building.domain.model.seamap;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.seamap.ResourceIslandType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_mining_resource_session")
@Getter
@Setter
@Accessors(chain = true)
public class SeaMiningSession extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long               id;
    private LocalDateTime      timeStart;
    private Double             duration;
    private Double             speed;
    private Double             collectedResource;
    private ResourceIslandType resourceType;
    private Double             tonnage; // remaining ship cap
    @OneToOne
    @JoinColumn(name = "sea_activity_id")
    @JsonIgnore
    private SeaActivity        seaActivity;
    @OneToOne(mappedBy = "miningSession")
    private ResourceIsland     resourceIsland;
    @Column(name = "isDeleted", columnDefinition = "boolean default false")
    private Boolean            isDeleted;

}
