package com.supergroup.kos.building.domain.model.config;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.supergroup.core.model.BaseModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class BaseShipConfig extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long   id;
    private String name;
    private String description;
    private Long   atk1;
    private Long   atk2;
    private Long   def1;
    private Long   def2;
    private Long   hp;
    private Long   dodge;
}
