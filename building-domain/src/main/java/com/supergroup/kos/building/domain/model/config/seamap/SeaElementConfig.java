package com.supergroup.kos.building.domain.model.config.seamap;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.model.seamap.OccupiedArea;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@Table(name = "tbl_elements_config")
@Entity
@Accessors(chain = true)
public class SeaElementConfig extends BaseModel {
    @Id
    @Access(AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private Long         level;
    private String       name;
    private String       thumbnail;
    @Embedded
    private OccupiedArea occupied;

    public SeaElementType getType() {
        if (this instanceof UserBaseConfig) {
            return SeaElementType.USER_BASE;
        } else if (this instanceof BossSeaConfig) {
            return SeaElementType.BOSS;
        } else if (this instanceof ResourceIslandConfig) {
            return SeaElementType.RESOURCE;
        } else if (this instanceof ShipElementConfig) {
            return SeaElementType.SHIP;
        } else {
            throw KOSException.of(ErrorCode.ELEMENT_NOT_FOUND);
        }

    }

}
