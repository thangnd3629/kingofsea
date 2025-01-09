package com.supergroup.kos.building.domain.service.seamap.item;

import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.constant.item.ItemId;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.item.UserItem;
import com.supergroup.kos.building.domain.repository.persistence.asset.AssetsRepository;
import com.supergroup.kos.building.domain.repository.persistence.item.UserItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Service
@RequiredArgsConstructor
public class UserItemService {
    @Delegate
    private final UserItemRepository userItemRepository;
    private final AssetsRepository   assetsRepository;

    public UserItem save(UserItem userItem) {
        return userItemRepository.save(userItem);
    }

    public UserItem getUserItem(Long kosProfileId, ItemId itemId) {
        Assets assets = assetsRepository.findByKosProfile_Id(kosProfileId).orElseThrow(() -> KOSException.of(
                ErrorCode.KOS_ASSETS_NOT_FOUND));
        return userItemRepository.findFirstByAsset_IdAndItem_IdAndIsUsed(assets.getId(), itemId, false).orElseThrow(
                () -> KOSException.of(ErrorCode.CAN_NOT_USE_ITEM));

    }
}
