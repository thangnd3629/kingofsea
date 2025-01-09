package com.supergroup.kos.building.domain.service.seamap.item;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetScoutBuildingInfoCommand;
import com.supergroup.kos.building.domain.constant.TechnologyCode;
import com.supergroup.kos.building.domain.constant.TechnologyType;
import com.supergroup.kos.building.domain.constant.UpgradeType;
import com.supergroup.kos.building.domain.constant.item.NameSpaceKey;
import com.supergroup.kos.building.domain.exception.TechRequirementException;
import com.supergroup.kos.building.domain.model.item.Item;
import com.supergroup.kos.building.domain.model.item.ItemEffect;
import com.supergroup.kos.building.domain.model.item.UseItemResult;
import com.supergroup.kos.building.domain.model.item.UserItem;
import com.supergroup.kos.building.domain.model.mining.ScoutBuilding;
import com.supergroup.kos.building.domain.model.technology.Technology;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.repository.persistence.item.UserItemRepository;
import com.supergroup.kos.building.domain.repository.persistence.upgrade.UpgradeSessionRepository;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.ScoutBuildingService;
import com.supergroup.kos.building.domain.service.ship.EscortShipService;
import com.supergroup.kos.building.domain.service.ship.MotherShipService;
import com.supergroup.kos.building.domain.service.upgrade.UpgradeService;
import com.supergroup.kos.building.domain.task.UpgradeTask;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpeedItemHandler implements ItemHandler {
    private final MotherShipService        motherShipService;
    private final UpgradeSessionRepository upgradeSessionRepository;
    private final UpgradeService           upgradeService;
    private final UserItemRepository       userItemRepository;
    private final ScoutBuildingService     scoutBuildingService;
    private final EscortShipService        escortShipService;
    private final AssetsService            assetsService;

    @Transactional
    @Override
    public UseItemResult applyItem(UserItem userItem, ApplyItemCommand command) throws JsonProcessingException {

        var asset = assetsService.findById(userItem.getAsset().getId()).orElseThrow(() -> KOSException.of(ErrorCode.KOS_ASSETS_NOT_FOUND));

        // Check use speed item permission
        // throw detail error with required technology
        if (!asset.getKosProfile().getCanUseSpeedItem()) {
            throw new TechRequirementException(ErrorCode.CAN_NOT_USE_SPEED_ITEM,
                                               new Technology().setCode(TechnologyCode.SC16)
                                                               .setTechnologyType(TechnologyType.SCIENCE));
        }

        var speedItemApplyCommand = (SpeedItemApplyCommand) command;

        var item = userItem.getItem();

        var reducedTime = getReducedTime(item);
        var now = LocalDateTime.now();
        if (speedItemApplyCommand.getTypeApplySpeedItem().equals(TypeApplySpeedItem.SCOUT_TRAINING)) {
            validItemBeforeUseTrainingScout(item);
            ScoutBuilding scoutBuilding = scoutBuildingService.getBuildingInfo(
                    new GetScoutBuildingInfoCommand(speedItemApplyCommand.getKosProfileId()));
            if (!Objects.nonNull(scoutBuilding.getIsTraining()) || !scoutBuilding.getIsTraining()) {
                throw KOSException.of(ErrorCode.BUILDING_SCOUT_NOT_IN_TRAINING);
            }
            scoutBuilding.setTrainingDuration(scoutBuilding.getTrainingDuration() - reducedTime);
            scoutBuildingService.save(scoutBuilding);

        } else if (speedItemApplyCommand.getTypeApplySpeedItem().equals(TypeApplySpeedItem.UPGRADE)) {
            var upgradeSession = upgradeSessionRepository.findById(speedItemApplyCommand.getUpgradeSessionId()).orElseThrow(
                    () -> KOSException.of(ErrorCode.BAD_REQUEST_ERROR));
            if(upgradeSession.getIsDeleted()) {
                throw KOSException.of(ErrorCode.UPGRADE_SESSION_IS_DONE);
            }
            validItemUpgradeBeforeUse(item, upgradeSession.getInfoInstanceModel().getType());
            switch (upgradeSession.getInfoInstanceModel().getType()) {
                case BUILDING:
                    if (reducedTime > upgradeSession.getDuration() ||
                        now.plusSeconds(reducedTime / 1000).isAfter(upgradeSession.getTimeStart().plusSeconds(upgradeSession.getDuration() / 1000))) {
                        upgradeService.completeUpgradeBuilding(upgradeSession);
                    } else {
                        upgradingAfterUseItemSpeed(upgradeSession, now, reducedTime);
                    }
                    break;
                case MOTHER_SHIP:
                    if (reducedTime > upgradeSession.getDuration() ||
                        now.plusSeconds(reducedTime / 1000).isAfter(upgradeSession.getTimeStart().plusSeconds(upgradeSession.getDuration() / 1000))) {
                        motherShipService.completeUpgradeMotherShip(upgradeSession);

                    } else {
                        upgradingAfterUseItemSpeed(upgradeSession, now, reducedTime);
                    }
                    break;
                case ESCORT_SHIP:
                    if (reducedTime > upgradeSession.getDuration() ||
                        now.plusSeconds(reducedTime / 1000).isAfter(upgradeSession.getTimeStart().plusSeconds(upgradeSession.getDuration() / 1000))) {
                        escortShipService.completeUpgradeLevelEscortShip(upgradeSession);

                    } else {
                        upgradingAfterUseItemSpeed(upgradeSession, now, reducedTime);
                    }
                    break;
                case ESCORT_BUILDING:
                    if (reducedTime > upgradeSession.getDuration() ||
                        now.plusSeconds(reducedTime / 1000).isAfter(upgradeSession.getTimeStart().plusSeconds(upgradeSession.getDuration() / 1000))) {
                        escortShipService.completeBuildEscortShip(upgradeSession);

                    } else {
                        upgradingAfterUseItemSpeed(upgradeSession, now, reducedTime);
                    }
                    break;
                default:
                    throw KOSException.of(ErrorCode.UPGRADE_SESSION_TYPE_NOT_FOUND);
            }
        } else {
            throw KOSException.of(ErrorCode.TYPE_APPLY_SPEED_ITEM_NOT_FOUND);
        }

        deactivateItem(userItem);
        return null; // TODO should be return meaningful value
    }

    @Override
    public void deactivateItem(UserItem item) {
        userItemRepository.delete(item);
    }

    private void validItemUpgradeBeforeUse(Item item, UpgradeType upgradeType) {
        Boolean isValid = false;
        switch (item.getNamespace()) {
            case UP_TIME:
                isValid = true;
                break;
            case UP_BUILD:
                isValid = Objects.equals(UpgradeType.BUILDING, upgradeType);
                break;
            case SHIP_BUILD:
                isValid = UpgradeType.MOTHER_SHIP.equals(upgradeType) || UpgradeType.ESCORT_SHIP.equals(upgradeType)
                          || UpgradeType.ESCORT_BUILDING.equals(upgradeType);
                break;
            default:
                throw KOSException.of(ErrorCode.SPEED_ITEM_ERROR);
        }
        if (!isValid) {
            throw KOSException.of(ErrorCode.CAN_NOT_USE_ITEM);
        }
    }

    private void validItemBeforeUseTrainingScout(Item item) {
        if (!item.getNamespace().equals(NameSpaceKey.UP_TIME)) {
            throw KOSException.of(ErrorCode.CAN_NOT_USE_ITEM);
        }
    }

    private Long getReducedTime(Item item) {
        if (item.getEffects().isEmpty()) {
            throw KOSException.of(ErrorCode.CAN_NOT_FOUND_ITEM_EFFECT_IN_SPEED_ITEM);
        }
        List<ItemEffect> list = (List<ItemEffect>) item.getEffects();
        Long reducedTime = 0L;
        for (ItemEffect itemEffect : list) {
            reducedTime += Double.valueOf(itemEffect.getParameter()).longValue();
        }
        return reducedTime;
    }

    private void upgradingAfterUseItemSpeed(UpgradeSession upgradeSession, LocalDateTime now, Long reducedTime) throws JsonProcessingException {
        upgradeSession.setDuration(upgradeSession.getDuration() - reducedTime);
        upgradeSessionRepository.save(upgradeSession);
        Long timeRemain = Duration.between(now, upgradeSession.getTimeStart().plusSeconds(upgradeSession.getDuration() / 1000)).toMillis();
        var task = new UpgradeTask().setUpgradeSessionId(upgradeSession.getId());
        upgradeService.sendUpgradeTaskToQueue(task, timeRemain);

    }
}
