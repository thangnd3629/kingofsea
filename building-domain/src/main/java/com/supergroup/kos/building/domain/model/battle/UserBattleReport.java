package com.supergroup.kos.building.domain.model.battle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.supergroup.core.model.BaseModel;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "tbl_user_battle_report")
public class UserBattleReport extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long          id;
    @ManyToOne
    @JoinColumn(name = "battle_profile_id")
    private BattleProfile battleProfile;
    @ManyToOne
    @JoinColumn(name = "battle_report_id")
    private BattleReport  battleReport;
    @Column(columnDefinition = "boolean default false")
    private Boolean       isDeleted = false;
}
