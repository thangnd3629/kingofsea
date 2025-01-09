package com.supergroup.kos.building.domain.model.seamap;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.supergroup.kos.building.domain.model.profile.KosProfile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Invader {
    @ManyToOne
    @JoinColumn(name = "kos_profile_invader_id")
    private KosProfile    kosProfileInvader;
    private LocalDateTime occupyAt;

    @Transient
    private List<SeaActivity> activitiesOnOccupiedBase;
}
