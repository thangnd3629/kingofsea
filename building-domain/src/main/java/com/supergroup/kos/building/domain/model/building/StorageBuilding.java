package com.supergroup.kos.building.domain.model.building;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.kos.building.domain.constant.StorageType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_storage_building")
@Getter
@Setter
@Accessors(chain = true)
public class StorageBuilding extends BaseBuilding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private StorageType storageType;

    @Transient
    private Double amount;
    @Transient
    private Long   capacity;
}
