package com.supergroup.kos.building.domain.model.seamap.reward;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@Table(name = "tbl_loadable_reward")
@Getter
@Setter
@Accessors(chain = true)
public class TaxableReward {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long        id;
    @Column(columnDefinition = "float8 default 0")
    private Double      stone = 0D;
    @Column(columnDefinition = "float8 default 0")
    private Double      wood  = 0D;
    @Column(columnDefinition = "float8 default 0")
    private Double      gold  = 0D;
    @Column(columnDefinition = "float8 default 0")
    private Double      remainingTonnage;
    @OneToOne
    @JoinColumn(name = "reward_id")
    private SeaReward   seaReward;
    @ManyToOne
    @JoinColumn(name = "move_session_id")
    private MoveSession moveSession;
}
