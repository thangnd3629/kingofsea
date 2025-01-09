package com.supergroup.kos.building.domain.service.seamap.item;

import java.util.Objects;

import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.async.SeaActivityAsyncTask;
import com.supergroup.kos.building.domain.command.GetScoutBuildingInfoCommand;
import com.supergroup.kos.building.domain.constant.battle.BattleCancelReason;
import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.constant.item.MoveBase;
import com.supergroup.kos.building.domain.exception.EffectIsActivatedException;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.item.ItemEffect;
import com.supergroup.kos.building.domain.model.item.UseItemResult;
import com.supergroup.kos.building.domain.model.item.UseMoveBaseItemResult;
import com.supergroup.kos.building.domain.model.item.UserItem;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.battle.BattlePvPService;
import com.supergroup.kos.building.domain.service.building.ScoutBuildingService;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;
import com.supergroup.kos.building.domain.service.seamap.activity.ActionPointService;
import com.supergroup.kos.notification.domain.command.SendNotificationCommand;
import com.supergroup.kos.notification.domain.constant.NotificationType;
import com.supergroup.kos.notification.domain.constant.SourceType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WarItemHandler implements ItemHandler {

    private final ScoutBuildingService scoutBuildingService;
    private final ActionPointService   actionPointService;
    private final AssetsService        assetsService;
    private final UserBaseService      userBaseService;
    private final BattlePvPService     battlePvPService;
    private final SeaActivityAsyncTask seaActivityAsyncTask;

    @Override
    @Transactional
    public UseItemResult applyItem(UserItem userItem, ApplyItemCommand command) {
        var item = userItem.getItem();
        var asset = assetsService.findById(userItem.getAsset().getId()).orElseThrow(() -> KOSException.of(ErrorCode.KOS_ASSETS_NOT_FOUND));
        for (ItemEffect itemEffect : item.getEffects()) {
            switch (itemEffect.getTarget()) {
                case CAN_SCOUT:
                    var building1 = scoutBuildingService.getBuildingInfo(new GetScoutBuildingInfoCommand(asset.getKosProfile().getId())
                                                                                 .setCheckUnlockBuilding(false));
                    // if this item already used, can not use it
                    if (!building1.canScout()) {
                        throw new EffectIsActivatedException("Anti Scout");
                    }
                    building1.setCanScout(false);
                    scoutBuildingService.save(building1);
                    break;
                case NUMBER_OF_SOLDIER:
                    var building2 = scoutBuildingService.getBuildingInfo(
                            new GetScoutBuildingInfoCommand(asset.getKosProfile().getId()));
                    // if this item already used, can not use it
                    if (building2.isBlindScout()) {
                        throw new EffectIsActivatedException("Blind Scout");
                    }
                    building2.setIsBlindScout(true);
                    building2.setBlindMulti(Double.valueOf(itemEffect.getParameter()));
                    scoutBuildingService.save(building2);
                    break;
                case BASE_LOCATION:
                    // change battle status
                    var userBase = userItem.getAsset().getKosProfile().getBase();
                    var battles = battlePvPService.getMovingBattleByUserBase(userBase.getId());
                    for (Battle battle : battles) {
                        battle.cancel(BattleCancelReason.MOVE_BASE);
                    }
                    battlePvPService.saveAll(battles);
                    // send query battle status request for client
                    seaActivityAsyncTask.sendQueryBattleStatusNotification(userItem.getAsset().getKosProfile().getUser().getId());
                    // move base
                    var type = MoveBase.valueOf(itemEffect.getParameter());
                    switch (type) {
                        case RANDOM:
                            var newRandomLocation = moveBaseRandom(asset.getKosProfile());
                            return new UseMoveBaseItemResult().setNewLocation(newRandomLocation);
                        case SPECIFIED:
                            var applyMoveBaseSpecifiedCommand = (ApplyMoveBaseSpecifiedCommand) command;
                            // validate data
                            if (Objects.isNull(applyMoveBaseSpecifiedCommand.getNewLocation())) {
                                throw KOSException.of(ErrorCode.NEW_LOCATION_IS_REQUIRED);
                            }
                            moveBaseToSpecified(asset.getKosProfile(), applyMoveBaseSpecifiedCommand.getNewLocation());
                            break;
                    }
                    break;
                default:
                    throw KOSException.of(ErrorCode.INVALID_ITEM_TARGET_TYPE);
            }
        }
        return null; // TODO should return meaningfull value
    }

    /**
     * @return new location
     */
    private Coordinates moveBaseRandom(KosProfile kosProfile) {
        checkBeforeMoveBase(kosProfile);
        return userBaseService.moveBaseUserRandom(kosProfile.getId());
    }

    private void moveBaseToSpecified(KosProfile kosProfile, Coordinates newLocation) {
        checkBeforeMoveBase(kosProfile);
        userBaseService.moveBaseUserCertain(kosProfile.getId(), newLocation);
    }

    private void checkBeforeMoveBase(KosProfile kosProfile) {
        // check action point
        var usedActionPoint = actionPointService.getUsedActionPoint(kosProfile.getId());
        if (usedActionPoint > 0) {
            throw KOSException.of(ErrorCode.CAN_NOT_MOVE_BASE_HAVE_SEA_ACTIVITY);
        }
        // check battle on user base?
        var userBase = kosProfile.getBase();
        if (Objects.nonNull(userBase.getBattle())) {
            if (userBase.getBattle().getStatus().equals(BattleStatus.PROGRESS)
                || userBase.getBattle().getStatus().equals(BattleStatus.BREAK)) {
                throw KOSException.of(ErrorCode.CAN_NOT_USE_MOVE_BASE_ITEM_WHEN_IN_COMBAT);
            }
        }
        // check user base status
        if (userBase.isOccupied()) {
            throw KOSException.of(ErrorCode.CAN_NOT_TELEPORT_WHEN_BASE_OCCUPIED);
        }
    }

    @Override
    @Transactional
    public void deactivateItem(UserItem userItem) {
        var item = userItem.getItem();
        var asset = assetsService.findById(userItem.getAsset().getId()).orElseThrow(() -> KOSException.of(ErrorCode.KOS_ASSETS_NOT_FOUND));
        item.getEffects().forEach(itemEffect -> {
            switch (itemEffect.getTarget()) {
                case CAN_SCOUT:
                    var building1 = scoutBuildingService.getBuildingInfo(
                            new GetScoutBuildingInfoCommand(asset.getKosProfile().getId()).setCheckUnlockBuilding(false));
                    building1.setCanScout(true);
                    scoutBuildingService.save(building1);
                    try {
                        // send notification to game mailbox
                        var sendNotificationCommand = new SendNotificationCommand(
                                null,
                                "CANCEL ANTI SCOUT",
                                "Cancel anti scout because you start scout",
                                null,
                                NotificationType.TOWN,
                                SourceType.SERVER,
                                userItem.getAsset().getKosProfile().getUser().getId()
                        );
//                        notificationService.sendDirect(null, null, null, null);
                        //todo : send noti war item
                    } catch (Exception ex) {
                        //ignore send notification error
                        ex.printStackTrace();
                    }
                    break;
                case NUMBER_OF_SOLDIER:
                    var building2 = scoutBuildingService.getBuildingInfo(
                            new GetScoutBuildingInfoCommand(asset.getKosProfile().getId()).setCheckUnlockBuilding(false));
                    building2.setIsBlindScout(false);
                    scoutBuildingService.save(building2);
                    try {
                        // send notification to game mailbox
                        var sendNotificationCommand = new SendNotificationCommand(
                                null,
                                "CANCEL BLIND SCOUT",
                                "Cancel blind scout because you start scout",
                                null,
                                NotificationType.TOWN,
                                SourceType.SERVER,
                                userItem.getAsset().getKosProfile().getUser().getId()
                        );
//                        notificationService.sendDirect(null, null, null, null);
                        //todo : send noti war item
                    } catch (Exception ex) {
                        //ignore send notification error
                        ex.printStackTrace();
                    }
                    break;
                default:
                    throw KOSException.of(ErrorCode.INVALID_ITEM_TARGET_TYPE);
            }
        });
    }

}
