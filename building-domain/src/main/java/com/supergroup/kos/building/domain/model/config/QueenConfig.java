package com.supergroup.kos.building.domain.model.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.supergroup.core.constant.BaseStatus;
import com.supergroup.core.model.BaseModel;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_queen_config")
@Getter
@Setter
@Accessors(chain = true)
public class QueenConfig extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long       id;
    private String     name;
    private Long       mp;
    private String     thumbnail;
    private BaseStatus status;
}
