package com.supergroup.kos.building.domain.model.config;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.supergroup.core.model.BaseModel;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "tbl_scout_case_config")
@Getter
@Setter
@Accessors(chain = true)
@Slf4j
public class ScoutCaseConfig extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

//    @Column(name = "army")
    private Long    numberArmy;
//    @Column(name = "enemy")
    private Long    numberEnemy;
    private Double  rateSuccess; // [0,1]
    private Double  rateDie; // [0,1]
    private Boolean isBetrayed;

    public Double getRateSuccess() {
        if(Objects.isNull(rateSuccess) || rateSuccess < 0){
            log.info("WARRING : Scout case id {} :  Invalid data rateSuccess ", id);
            return 0D;
        }
        return Math.min(rateSuccess, 1D);
    }

    public Double getRateDie() {
        if(Objects.isNull(rateDie) || rateDie < 0){
            log.info("WARRING : Scout case id {} :  Invalid data rateDie ", id);
            return 0D;
        }
        return Math.min(rateDie, 1D);
    }
}
