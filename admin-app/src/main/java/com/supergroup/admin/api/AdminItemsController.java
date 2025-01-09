package com.supergroup.admin.api;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.admin.dto.ItemResponse;
import com.supergroup.admin.dto.request.AddItemsRequest;
import com.supergroup.admin.mapper.AdminItemsMapper;
import com.supergroup.asset.service.AssetService;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.constant.item.ItemType;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.item.Item;
import com.supergroup.kos.building.domain.model.item.UserItem;
import com.supergroup.kos.building.domain.repository.persistence.item.ItemRepository;
import com.supergroup.kos.building.domain.repository.persistence.item.UserItemRepository;
import com.supergroup.kos.building.domain.service.asset.AssetsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/items")
@RequiredArgsConstructor
public class AdminItemsController {
    private final UserItemRepository userItemRepository;
    private final ItemRepository   itemRepository;
    private final AdminItemsMapper itemsMapper;
    private final AssetService     assetService;
    private final AssetsService      assetsService;

    @GetMapping
    public ResponseEntity<?> getAllItems(@RequestParam(name = "type") ItemType type) {
        List<Item> listItem = itemRepository.findByType(type);
        List<ItemResponse> responses = listItem.stream().map(item -> {
            ItemResponse itemResponse = itemsMapper.toResponse(item);
            if (Objects.nonNull(itemResponse.getThumbnail())) {
                itemResponse.setThumbnail(assetService.getUrl(item.getThumbnail()));
            }
            return itemResponse;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<?> addItems(@RequestBody AddItemsRequest request) {
        Assets assets = assetsService.getAssets(new KosProfileCommand().setKosProfileId(request.getKosProfileId()));
        Item item = itemRepository.findById(request.getItemId()).orElseThrow(() -> KOSException.of(ErrorCode.ITEM_NOT_FOUND));
        UserItem userItem = new UserItem();
        userItem.setIsUsed(false)
                .setItem(item)
                .setAsset(assets);
        userItemRepository.save(userItem);
        return ResponseEntity.ok().build();
    }
}
