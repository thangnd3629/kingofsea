package com.supergroup.kos.building.domain.model.battle;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.supergroup.kos.building.domain.constant.battle.FlatCheckResultBattle;

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
public class CheckResultBattle {
    @Enumerated(EnumType.STRING)
    private FlatCheckResultBattle attackerCheckResult;
    @Enumerated(EnumType.STRING)
    private FlatCheckResultBattle defenderCheckResult;
}
