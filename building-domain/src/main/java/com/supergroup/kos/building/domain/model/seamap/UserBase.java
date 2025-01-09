package com.supergroup.kos.building.domain.model.seamap;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.model.profile.KosProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@DiscriminatorValue("USER")
@Getter
@Setter
@Accessors(chain = true)
public class UserBase extends SeaElement {
    @Column(name = "user_island_name")
    private String     islandName;
    @OneToOne
    @JoinColumn(name = "kos_profile_id")
    private KosProfile kosProfile;
    @Column(name = "user_base_npc_elements")
    private String     npcElements;
    @Column(name = "user_base_is_ready", columnDefinition = "boolean default true")
    private Boolean    isReady = true;

    public LocalDateTime initLiberateAt;

    @Override
    public SeaElementType type() {
        return SeaElementType.USER_BASE;
    }

    @Override
    public String name() {
        return islandName;
    }
}
