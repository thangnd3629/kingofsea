package com.supergroup.kos.building.domain.model.seamap;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.supergroup.kos.building.domain.constant.seamap.BossSeaStatus;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@DiscriminatorValue("BOSS")
@Getter
@Setter
@Accessors(chain = true)
public class BossSea extends SeaElement {

    @Column(name = "boss_hp_lost")
    private Long          hpLost;
    @Column(name = "boss_status")
    private BossSeaStatus status;
    @Column(name = "boss_time_reviving_end")
    private LocalDateTime timeRevivingEnd;

    @Transient
    private BattleProfile battleProfile;

    @Override
    public SeaElementType type() {
        return SeaElementType.BOSS;
    }

    @Override
    public String name() {
        return getSeaElementConfig().getName();
    }
}
