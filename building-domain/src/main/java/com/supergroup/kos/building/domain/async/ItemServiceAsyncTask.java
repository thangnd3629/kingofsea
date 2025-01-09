package com.supergroup.kos.building.domain.async;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.supergroup.asset.service.AssetService;
import com.supergroup.core.constant.NotificationRenderContentPlaceHolder;
import com.supergroup.kos.building.domain.model.item.Item;
import com.supergroup.kos.building.domain.model.item.UserItem;
import com.supergroup.kos.building.domain.repository.persistence.item.UserItemRepository;
import com.supergroup.kos.notification.domain.model.NotificationTemplateType;
import com.supergroup.kos.notification.domain.service.NotificationTemplateService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ItemServiceAsyncTask {
    private final NotificationTemplateService notificationTemplateService;
    private final UserItemRepository  userItemRepository;
    private final AssetService        assetService;

    @Async
    public void sendUseItemNotification(Long userId, Item item) {
        Map<String, Object> namedParams = new HashMap<>();
        namedParams.put(NotificationRenderContentPlaceHolder.ITEM_NAME, item.getName());
        notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.APPLY_ITEM, namedParams, null, null);
    }

    @Transactional
    public void sendExpiredItemNotification(Long userItemId) {
        Optional<UserItem> optional = userItemRepository.findById(userItemId);
//        if (!optional.isPresent()) {
//            return;
//        }
//        UserItem userItem = optional.get();
//        var item = userItem.getItem();
//        try {
//            var title = "ITEM EXPIRED";
//            var body = "Item " + item.getName() + " has expired.";
//            var metadata = MapUtils.toString(
//                    Map.of("itemId", item.getId(), "status", "expired", "thumbnail", assetService.getUrl(item.getThumbnail())));
//            KosMessage kosMessage = new KosMessage().setData(metadata).setBody(body).setIntent(MessageType.TOWN_NOTIFICATION_NEW.getIntent());
//            var command = new SendNotificationCommand(kosMessage, title, body,
//                                                      metadata, NotificationType.TOWN, SourceType.SERVER,
//                                                      userItem.getAsset().getKosProfile().getUser().getId());
//            notificationService.sendDirect(command);
//        } catch (KOSException ex) {
//            if (!ex.getCode().equals(ErrorCode.USER_MUST_REGISTER_NOTIFY)) {
//                throw ex;
//            }
//        }
    }
}
