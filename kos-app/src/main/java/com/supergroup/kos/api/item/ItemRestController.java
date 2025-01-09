package com.supergroup.kos.api.item;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.supergroup.asset.service.AssetService;
import com.supergroup.kos.building.domain.command.UseItemCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.item.ItemId;
import com.supergroup.kos.building.domain.constant.item.ItemType;
import com.supergroup.kos.building.domain.constant.item.NameSpaceKey;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.seamap.item.ItemService;
import com.supergroup.kos.dto.item.DetailUserItemResponse;
import com.supergroup.kos.dto.item.ItemsResponse;
import com.supergroup.kos.dto.item.UseItemRequest;
import com.supergroup.kos.mapper.ItemsMapper;
import com.supergroup.kos.mapper.UseItemResultMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/user/items")
@RequiredArgsConstructor
public class ItemRestController {
    private final ItemService         itemService;
    private final KosProfileService   kosProfileService;
    private final AssetService        assetService;
    private final ItemsMapper         itemsMapper;
    private final UseItemResultMapper useItemResultMapper;

    @GetMapping
    private ResponseEntity<?> getAllItemsOfUser(@RequestParam(name = "type") ItemType type,
                                                @RequestParam(name = "namespace", required = false) List<NameSpaceKey> namespace) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var listItem = itemService.getAllItemOfUser(kosProfile.getId(), type, namespace);
        if (namespace != null) {
            listItem = listItem.stream().filter(is -> namespace.contains(NameSpaceKey.valueOf(is.getNamespace()))).collect(Collectors.toList());
        }
        List<ItemsResponse> response = listItem.stream().map(item -> {
            ItemsResponse itemsResponse = itemsMapper.toResponse(item);
            if (Objects.nonNull(itemsResponse.getThumbnail())) {
                itemsResponse.setThumbnail(assetService.getUrl(item.getThumbnail()));
            }
            return itemsResponse;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{itemId}")
    private ResponseEntity<DetailUserItemResponse> getItemDetails(@PathVariable("itemId") ItemId itemId) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var userItem = itemService.getItemDetails(kosProfile, itemId);
        var res = itemsMapper.toResponse(userItem);
        res.setThumbnail(assetService.getUrl(res.getThumbnail()));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/use")
    private ResponseEntity<?> useItem(@RequestBody @Valid UseItemRequest request) throws JsonProcessingException {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        UseItemCommand useItemCommand = new UseItemCommand().setKosProfileId(kosProfile.getId())
                                                            .setItemId(request.getItemId())
                                                            .setAmount(request.getAmount())
                                                            .setUpgradeSessionId(request.getUpgradeSessionId())
                                                            .setTypeApplySpeedItem(request.getTypeApplySpeedItem());
        if (Objects.nonNull(request.getNewLocation())) {
            useItemCommand.setNewLocation(new Coordinates(request.getNewLocation().getX(), request.getNewLocation().getY()));
        }
        var useItemResults = itemService.useItem(useItemCommand);
        if (request.getItemId().equals(ItemId.WA_11)
            || request.getItemId().equals(ItemId.WA_12)
               && !useItemResults.isEmpty()) {
            return ResponseEntity.ok(useItemResultMapper.map(useItemResults.get(0)));
        }
        return ResponseEntity.ok().build();
    }

}
