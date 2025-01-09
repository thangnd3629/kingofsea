package com.supergroup.kos.building.domain.service.seamap.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.supergroup.kos.building.domain.model.item.UseItemResult;
import com.supergroup.kos.building.domain.model.item.UserItem;

public interface ItemHandler {

    UseItemResult applyItem(UserItem userItem, ApplyItemCommand command) throws JsonProcessingException;

    void deactivateItem(UserItem item);
}
