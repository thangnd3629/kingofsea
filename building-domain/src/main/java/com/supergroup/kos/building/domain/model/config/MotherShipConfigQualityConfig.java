package com.supergroup.kos.building.domain.model.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.supergroup.core.constant.BaseStatus;
import com.supergroup.core.model.BaseModel;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_mother_ship_config_quality_config")
@Getter
@Setter
@Accessors(chain = true)
public class MotherShipConfigQualityConfig extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long                    id;
    private Long                    gold;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mother_ship_config_id")
    private MotherShipConfig        motherShipConfig;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mother_ship_quality_config_id")
    private MotherShipQualityConfig motherShipQualityConfig;
    private BaseStatus              status;
}
