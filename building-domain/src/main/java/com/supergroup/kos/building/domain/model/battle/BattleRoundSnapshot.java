package com.supergroup.kos.building.domain.model.battle;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.model.battle.logic.Attack;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_battle_round_snapshot")
@Getter
@Setter
@Accessors(chain = true)
public class BattleRoundSnapshot extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private Long currentRound;

    //    @ManyToOne
//    @JoinColumn(name = "battle_field_config_id")
//    private BattleFieldConfig battleFieldConfig;
    @Transient
    private Attack attackerModel;
    @Basic
    @Column(name = "attacker", columnDefinition = "TEXT")
    private String attacker;

    @Transient
    private Attack defenderModel;
    @Basic
    @Column(name = "defender", columnDefinition = "TEXT")
    private String defender;

    @OneToOne(mappedBy = "battleRoundSnapshot", orphanRemoval = true)
    private BattleRound battleRound;

    @PostLoad
    void load() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(new JavaTimeModule());
        this.attackerModel = mapper.readValue(this.attacker, Attack.class);
        this.defenderModel = mapper.readValue(this.defender, Attack.class);
    }

    @PrePersist
    @PreUpdate
    void persist() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(new JavaTimeModule());
        this.attacker = mapper.writeValueAsString(this.attackerModel);
        this.defender = mapper.writeValueAsString(this.defenderModel);
    }

}
