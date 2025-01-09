package com.supergroup.kos.building.domain.service.seamap.activity.arrivalHandler;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.supergroup.kos.building.domain.command.WithdrawActivityCommand;
import com.supergroup.kos.building.domain.constant.seamap.SeaActivityStatus;
import com.supergroup.kos.building.domain.model.scout.Scout;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;
import com.supergroup.kos.building.domain.service.scout.ScoutMode;
import com.supergroup.kos.building.domain.service.scout.ScoutModeFactory;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ScoutMoveSessionHandler implements MoveSessionHandler<UserBase> {
    private final ScoutModeFactory   scoutModeFactory;
    private final SeaActivityService seaActivityService;

    @Override
    public void handleMove(UserBase enemyBase, MoveSession session, SeaActivity activity) {
        Scout scout = activity.getScout();
        ScoutMode scoutMode = scoutModeFactory.getMode(scout);
        if (Objects.nonNull(scoutMode)) {
            scoutMode.scoutInEnemyPlace(scout);
        }
        seaActivityService.withdraw(new WithdrawActivityCommand().setId(activity.getId()));
        activity.setStatus(SeaActivityStatus.SCOUTING);
        seaActivityService.save(activity);
    }
}
