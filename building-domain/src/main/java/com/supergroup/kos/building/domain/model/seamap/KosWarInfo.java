package com.supergroup.kos.building.domain.model.seamap;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "tbl_kos_war_info")
@Getter
@Setter
@Accessors(chain = true)
public class KosWarInfo extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private Long power             = 0L;
    private Long win               = 0L;
    private Long lose              = 0L;
    private Long bossKilled        = 0L;
    private Long warshipsDestroyed = 0L;
    private Long warshipsLost      = 0L;

    @OneToOne
    @JoinColumn(name = "kos_profile_id")
    private KosProfile kosProfile;

}
