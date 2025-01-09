package com.supergroup.kos.building.domain.model.battle;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.BattleProfileType;
import com.supergroup.kos.building.domain.constant.battle.FactionType;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "tbl_battle_profile")
public class BattleProfile extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String      username; //todo
    private String      avatar;  //todo
    @Embedded
    private Coordinates coordinates;

    @OneToMany(mappedBy = "battleProfile")
    private List<ShipLineUp> shipLineUps = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "battle_id")
    private Battle battle;

    @ManyToOne
    @JoinColumn(name = "battle_report_id")
    private BattleReport battleReport;

    @ManyToOne
    @JoinColumn(name = "kos_profile_id", nullable = false)
    private KosProfile kosProfile;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "boss_id")),
            @AttributeOverride(name = "configId", column = @Column(name = "boss_config_id")),
    })
    private BossSeaEmbedded bossSea;

    @Enumerated(EnumType.STRING)
    private FactionType faction;

    @Enumerated(EnumType.STRING)
    private BattleProfileType type;

}
