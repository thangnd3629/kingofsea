package com.supergroup.kos.building.domain.model.seamap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.supergroup.core.model.BaseModel;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_colonization_activity")
@Getter
@Setter
@Accessors(chain = true)
public class ColonizationActivity extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long          id;
    @ManyToOne
    @JoinColumn(name = "user_base_id")
    private UserBase      userBase;
    @ManyToOne
    @JoinColumn(name = "activity_id")
    private SeaActivity   activity;

}
