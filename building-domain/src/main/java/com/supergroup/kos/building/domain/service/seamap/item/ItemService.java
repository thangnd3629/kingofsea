package com.supergroup.kos.building.domain.service.seamap.item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.async.ItemServiceAsyncTask;
import com.supergroup.kos.building.domain.async.SeaActivityAsyncTask;
import com.supergroup.kos.building.domain.command.UseItemCommand;
import com.supergroup.kos.building.domain.constant.item.ItemId;
import com.supergroup.kos.building.domain.constant.item.ItemType;
import com.supergroup.kos.building.domain.constant.item.NameSpaceKey;
import com.supergroup.kos.building.domain.dto.item.DetailUserItem;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.item.Item;
import com.supergroup.kos.building.domain.model.item.UseItemResult;
import com.supergroup.kos.building.domain.model.item.UserItem;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.repository.persistence.asset.AssetsRepository;
import com.supergroup.kos.building.domain.repository.persistence.item.ItemRepository;
import com.supergroup.kos.building.domain.repository.persistence.item.UserItemRepository;
import com.supergroup.kos.building.domain.repository.persistence.upgrade.UpgradeSessionRepository;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.battle.BattlePvPService;
import com.supergroup.kos.building.domain.service.building.ScoutBuildingService;
import com.supergroup.kos.building.domain.service.building.StorageBuildingService;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;
import com.supergroup.kos.building.domain.service.seamap.activity.ActionPointService;
import com.supergroup.kos.building.domain.service.ship.EscortShipService;
import com.supergroup.kos.building.domain.service.ship.MotherShipService;
import com.supergroup.kos.building.domain.service.upgrade.UpgradeService;
import com.supergroup.kos.building.domain.task.ExpiredItemTask;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {

    private static final int THRESHOLD = 2000;

    private final UserItemRepository       userItemRepository;
    private final ScoutBuildingService     scoutBuildingService;
    private final ItemRepository           itemRepository;
    private final AssetsRepository         assetsRepository;
    private final AssetsService            assetsService;
    private final StorageBuildingService   storageBuildingService;
    private final MotherShipService        motherShipService;
    private final UpgradeSessionRepository upgradeSessionRepository;
    private final UpgradeService           upgradeService;
    private final ItemServiceAsyncTask     itemServiceAsyncTask;
    private final ObjectMapper             objectMapper;
    private final RabbitTemplate           rabbitTemplate;
    private final EscortShipService        escortShipService;
    private final UserBaseService          userBaseService;
    private final ActionPointService       actionPointService;
    private final BattlePvPService         battlePvPService;
    private final SeaActivityAsyncTask     seaActivityAsyncTask;

    public Page<Item> getAllItems(ItemType itemType, Pageable pageable) {
        return itemRepository.getAllItem(itemType, pageable);
    }

    public List<Item> getAllItems() {
        return itemRepository.getAll();
    }

    public List<DetailUserItem> getAllItemOfUser(Long kosProfileId, ItemType itemType, List<NameSpaceKey> listNamespace) {
        Assets assets = assetsRepository.findByKosProfile_Id(kosProfileId).orElseThrow(() -> KOSException.of(ErrorCode.KOS_ASSETS_NOT_FOUND));
        return userItemRepository.getUserItemByInNamespace(assets.getId(), itemType, listNamespace);
    }

    @Transactional
    public List<UseItemResult> useItem(UseItemCommand command) throws JsonProcessingException {
        Assets assets = assetsRepository.findByKosProfile_Id(command.getKosProfileId())
                                        .orElseThrow(() -> KOSException.of(ErrorCode.KOS_ASSETS_NOT_FOUND));
        UserItem userItem = userItemRepository.findFirstByAsset_IdAndItem_IdAndIsUsed(assets.getId(), command.getItemId(), false)
                                              .orElseThrow(() -> KOSException.of(ErrorCode.YOU_DO_NOT_HAVE_THIS_ITEM));

        if (userItem.getItem().getType().equals(ItemType.SPEED)) {
            // check validation of speed item type
            if (Objects.isNull(command.getTypeApplySpeedItem())) {
                throw KOSException.of(ErrorCode.CAN_NOT_FOUND_TYPE_ITEM_SPEED_APPLY);
            }
            command.setItemType(ItemType.SPEED);
            // You can use only 1 speed item at time
            command.setAmount(1L);
        }

        // Apply items
        var result = new ArrayList<UseItemResult>();
        for (int i = 0; i < command.getAmount(); i++) {
            var applyItemCommand = toApplyItemCommand(command);
            result.add(apply(userItem, applyItemCommand));
        }
        // After using item complete, send notification and message for client
        itemServiceAsyncTask.sendUseItemNotification(userItem.getAsset().getKosProfile().getUser().getId(), userItem.getItem());
        return result;
    }

    /**
     * Apply item
     */
    public UseItemResult apply(UserItem userItem, ApplyItemCommand command) throws JsonProcessingException {
        var itemHandler = createItemHandler(userItem.getItem().getType());
        // apply item
        var res = itemHandler.applyItem(userItem, command);
        // post apply item
        // delete user item which do not have expiry
        if (Objects.isNull(userItem.getItem().getExpiry())) {
            userItemRepository.delete(userItem);
        } else {
            // otherwise set is used and set expired date
            userItem.setIsUsed(true);
            var now = LocalDateTime.now();
            userItem.setUseTime(now);
            userItem.setDuration(userItem.getItem().getExpiry());
            if (Objects.nonNull(userItem.getItem().getExpiry())) {
                Long expiry = userItem.getItem().getExpiry();
                userItem.setExpiredDate(now.plusSeconds(expiry));
                sendCancelItemTaskToQueue(new ExpiredItemTask().setUseItemId(userItem.getId()), expiry * 1000);
            }
            userItemRepository.save(userItem);

        }
        return res;
    }

    private ApplyItemCommand toApplyItemCommand(UseItemCommand useItemCommand) {
        if (Objects.nonNull(useItemCommand.getItemType()) && useItemCommand.getItemType().equals(ItemType.SPEED)) {
            return new SpeedItemApplyCommand()
                    .setKosProfileId(useItemCommand.getKosProfileId())
                    .setUpgradeSessionId(useItemCommand.getUpgradeSessionId())
                    .setTypeApplySpeedItem(useItemCommand.getTypeApplySpeedItem());
        } else if (useItemCommand.getItemId().equals(ItemId.WA_12)) {
            return new ApplyMoveBaseSpecifiedCommand(useItemCommand.getNewLocation());
        }
        return null;
    }

    @Transactional
    public void deactivate(UserItem userItem) {
        var itemHandler = createItemHandler(userItem.getItem().getType());
        itemHandler.deactivateItem(userItem);
        userItemRepository.delete(userItem);
    }

    /**
     * provide item handler by item type
     */
    private ItemHandler createItemHandler(ItemType type) {
        switch (type) {
            case WAR:
                return new WarItemHandler(scoutBuildingService,
                                          actionPointService,
                                          assetsService,
                                          userBaseService,
                                          battlePvPService,
                                          seaActivityAsyncTask);
            case RESOURCE:
                return new ResourceItemHandler(assetsService, storageBuildingService);
            case SPEED:
                return new SpeedItemHandler(motherShipService,
                                            upgradeSessionRepository,
                                            upgradeService,
                                            userItemRepository,
                                            scoutBuildingService,
                                            escortShipService,
                                            assetsService);
            default:
                throw KOSException.of(ErrorCode.INVALID_ITEM_TYPE);
        }
    }

    /**
     * Check expired item
     */
    public Boolean isExpired(UserItem userItem) {
        if (Objects.isNull(userItem.getExpiredDate())) {return false;}
        return userItem.getIsUsed() && LocalDateTime.now().isAfter(userItem.getExpiredDate());
    }

    public void sendCancelItemTaskToQueue(ExpiredItemTask task, Long duration) throws JsonProcessingException {
        var taskJson = objectMapper.writeValueAsString(task);
        var prop = new MessageProperties();
        prop.setHeader("x-delay", duration - THRESHOLD);
        var mess = MessageBuilder.withBody(taskJson.getBytes())
                                 .andProperties(prop)
                                 .build();
        rabbitTemplate.convertAndSend("expired-item-exchange", "expired-item", mess);
    }

    @Transactional
    public UserItem getItemDetails(KosProfile kosProfile, ItemId itemId) {
        var assetId = kosProfile.getAssets().getId();
        var item = itemRepository.findById(itemId)
                                 .orElseThrow(() -> KOSException.of(ErrorCode.ITEM_NOT_FOUND));
        var amount = userItemRepository.amountItemByItemIdAndAssetId(assetId, itemId);
        var userItem = new UserItem().setItem(item)
                                     .setAsset(kosProfile.getAssets())
                                     .setIsUsed(false)
                                     .setAmount(amount);

        var detailItemEffect = userItemRepository.getDetailItemEffect(assetId, itemId.name());
        if (detailItemEffect.isPresent()) {
            userItem.setExpiredDate(detailItemEffect.get().getExpiredDate());
            userItem.setUseTime(detailItemEffect.get().getUseTime());
            userItem.setIsUsed(detailItemEffect.get().getIsUsed());
            userItem.setIsExpired(isExpired(userItem));
        }
        return userItem;
    }
}
