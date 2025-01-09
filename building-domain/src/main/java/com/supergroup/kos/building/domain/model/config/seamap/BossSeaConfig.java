package com.supergroup.kos.building.domain.model.config.seamap;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PostLoad;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.supergroup.kos.building.domain.constant.seamap.BossSeaType;
import com.supergroup.kos.building.domain.model.seamap.BossMayGetReward;
import com.supergroup.kos.building.domain.model.seamap.BossWillGetReward;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@DiscriminatorValue("BOSS")
@Getter
@Setter
@Accessors(chain = true)
public class BossSeaConfig extends SeaElementConfig {
    @Column(name = "boss_type")
    @Enumerated(EnumType.STRING)
    private BossSeaType       bossType;
    @Column(name = "boss_atk1")
    private Long              bossAtk1;
    @Column(name = "boss_atk2")
    private Long              bossAtk2;
    @Column(name = "boss_hp")
    private Long              bossHp;
    @Column(name = "boss_def1")
    private Double            bossDef1;
    @Column(name = "boss_def2")
    private Double            bossDef2;
    @Column(name = "boss_dodge")
    private Double            bossDodge;
    @Column(name = "boss_time_respawn")
    private Long              bossTimeRespawn; //seconds
    @Embedded
    private BossWillGetReward willGet;

    @Basic
    @Column(name = "reward_item")
    private String           mayGetValue;
    @Transient
    private BossMayGetReward mayGet;

    @PostLoad
    void postLoad() throws JsonProcessingException {
        var objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(new JavaTimeModule());
        mayGet = objectMapper.readValue(mayGetValue, BossMayGetReward.class);
    }

}
