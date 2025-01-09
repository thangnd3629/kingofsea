package com.supergroup.kos.building.domain.model.point;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.model.profile.KosProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_point")
@Getter
@Setter
@Accessors(chain = true)
public class Point extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private Long mpPoint;
    private Long gpPoint;
    private Long tpPoint;
    private Long actionPoint;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kos_profile_id")
    private KosProfile kosProfile;
}
