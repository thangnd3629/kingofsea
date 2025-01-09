package com.supergroup.kos.building.domain.model.upgrade;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.model.profile.KosProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_upgrade_session")
@Getter
@Setter
@Accessors(chain = true)
public class UpgradeSession extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private LocalDateTime     timeStart;
    private Long              duration; // milis
    private Boolean           isDeleted = false;
    @Embedded
    private InfoInstanceModel infoInstanceModel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kos_profile_id")
    private KosProfile kosProfile;

    public Boolean getIsDeleted(){
        return Objects.nonNull(isDeleted) ? isDeleted : true;
    }
}
