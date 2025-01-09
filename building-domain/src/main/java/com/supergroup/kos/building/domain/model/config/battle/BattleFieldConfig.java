package com.supergroup.kos.building.domain.model.config.battle;

import java.lang.reflect.Type;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.supergroup.core.model.BaseModel;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_battle_field_config")
@Getter
@Setter
@Accessors(chain = true)
public class BattleFieldConfig extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long                    id;
    //    private Integer                 threshold;
    private Integer                 minShip;
    private String                  name;
    @Transient
    private BattleFieldLineUpConfig battleFieldLineupModelConfig;
    @Basic
    @Column(name = "battle_field_lineup", columnDefinition = "TEXT")
    private String                  battlefieldLineup;

    @PostLoad
    void load() {
        Gson gson = new Gson();
        Type type = new TypeToken<BattleFieldLineUpConfig>() {}.getType();
        this.battleFieldLineupModelConfig = gson.fromJson(this.battlefieldLineup, type);
    }

    @PrePersist
    @PreUpdate
    void persist() {
        Gson gson = new Gson();
        this.battlefieldLineup = gson.toJson(this.battleFieldLineupModelConfig);
    }

}
