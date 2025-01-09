package com.supergroup.kos.building.domain.model.battle;

import java.time.LocalDateTime;
import java.util.Objects;

import com.supergroup.auth.domain.model.User;
import com.supergroup.auth.domain.model.UserProfile;
import com.supergroup.kos.building.domain.constant.BattleProfileType;
import com.supergroup.kos.building.domain.constant.battle.BattleResult;
import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.constant.battle.BattleType;
import com.supergroup.kos.building.domain.constant.seamap.ResourceIslandType;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.UserBase;

public interface IBattleReport {
    Long getBattleId();

    BattleType getBattleType();

    BattleProfileType getAttackerType();

    Long getAttackerKosProfileId();

    Long getAttackerId();

    Long getAttackerX();

    Long getAttackerY();

    String getAttackerName();

    String getAttackerAvatarUrl();

    BattleProfileType getDefenderType();

    Long getDefenderKosProfileId();

    Long getDefenderId();

    Long getDefenderX();

    Long getDefenderY();

    String getDefenderName();

    String getDefenderAvatarUrl();

    Long getDefenderBossId();

    Long getDefenderBossConfigId();

    Long getWinnerId();

    Long getLoserId();

    Long getX();

    Long getY();

    ResourceIslandType getResourceType();

    BattleStatus getStatus();

    LocalDateTime getStartAt();

    LocalDateTime getEndAt();

    LocalDateTime getUpdatedAt();

    Long getAmountRound();

    default BattleProfile initiatorProfile() {
        var user = new User();
        var userProfile = new UserProfile();
        userProfile.setUsername(getAttackerName())
                   .setAvatarUrl(getAttackerAvatarUrl())
                   .setUser(user);

        user.setUserProfile(userProfile);

        var base = new UserBase();
        base.setCoordinate(new Coordinates(getAttackerX(), getAttackerY()));

        var kosProfile = new KosProfile().setId(getAttackerKosProfileId())
                                         .setUser(user)
                                         .setBase(base);

        return new BattleProfile().setKosProfile(kosProfile)
                                  .setId(getAttackerId())
                                  .setCoordinates(new Coordinates(getAttackerX(), getDefenderY()))
                                  .setType(getAttackerType());
    }

    default BattleProfile victimProfile() {
        var user = new User();
        var userProfile = new UserProfile();
        userProfile.setUsername(getDefenderName())
                   .setAvatarUrl(getDefenderAvatarUrl())
                   .setUser(user);

        user.setUserProfile(userProfile);

        var base = new UserBase();
        base.setCoordinate(new Coordinates(getDefenderX(), getDefenderY()));

        var kosProfile = new KosProfile().setId(getDefenderKosProfileId())
                                         .setUser(user)
                                         .setBase(base);

        var boss = new BossSeaEmbedded().setId(getDefenderBossId())
                                        .setConfigId(getDefenderBossConfigId());

        Coordinates coordinate;

        coordinate = new Coordinates(getDefenderX(), getDefenderY());

        return new BattleProfile().setKosProfile(kosProfile)
                                  .setBossSea(boss)
                                  .setId(getDefenderId())
                                  .setCoordinates(coordinate)
                                  .setType(getDefenderType());
    }

    default Coordinates coordinate() {
        return new Coordinates(getX(), getY());
    }

    default BattleProfile winnerProfile() {
        if (Objects.isNull(getWinnerId())) {
            return null;
        }
        return getAttackerId().equals(getWinnerId()) ? initiatorProfile() : victimProfile();
    }

    default BattleProfile loserProfile() {
        if (Objects.isNull(getLoserId())) {
            return null;
        }
        return getAttackerId().equals(getLoserId()) ? initiatorProfile() : victimProfile();
    }

    default BattleResult result(Long yourKosProfileId) {
        if (getStatus().equals(BattleStatus.END)) {
            if (Objects.isNull(getWinnerId())) {
                return BattleResult.UNDEFINED;
            } else if (getDefenderType().equals(BattleProfileType.USER)) {
                if (yourKosProfileId.equals(winnerProfile().getKosProfile().getId())) {
                    return BattleResult.WIN;
                } else if (yourKosProfileId.equals(loserProfile().getKosProfile().getId())) {
                    return BattleResult.LOSE;
                } else {
                    return BattleResult.UNDEFINED;
                }
            } else {
                if (winnerProfile().getType().equals(BattleProfileType.BOSS)) {
                    return BattleResult.LOSE;
                } else if (yourKosProfileId.equals(winnerProfile().getKosProfile().getId())) {
                    return BattleResult.WIN;
                } else {
                    return BattleResult.UNDEFINED;
                }
            }
        }
        return null;
    }
}
