package com.supergroup.kos.api.item;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.asset.service.AssetService;
import com.supergroup.kos.building.domain.model.item.Item;
import com.supergroup.kos.building.domain.service.seamap.item.ItemService;
import com.supergroup.kos.dto.item.ItemConfigResponse;
import com.supergroup.kos.mapper.ItemsMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/item")
@RequiredArgsConstructor
public class ItemModeLRestController {
    private final ItemService itemService;
    private final ItemsMapper itemsMapper;
    private final AssetService assetService;

    @GetMapping("/config")
    public ResponseEntity<?> getAll() {
        List<Item> listItems = itemService.getAllItems();
        List<ItemConfigResponse> result = itemsMapper.toConfigDTOS(listItems).stream().map(
                        itemConfigResponse -> itemConfigResponse.setThumbnail(assetService.getUrl(itemConfigResponse.getThumbnail()))
                ).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
