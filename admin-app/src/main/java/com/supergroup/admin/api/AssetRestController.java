package com.supergroup.admin.api;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.admin.domain.command.AdminUpdateAssetCommand;
import com.supergroup.admin.domain.service.AdminAssetService;
import com.supergroup.admin.dto.AssetResponse;
import com.supergroup.admin.mapper.AssetMapper;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/asset")
@RequiredArgsConstructor
public class AssetRestController {

    private final AdminAssetService adminAssetService;
    private final KosProfileService kosProfileService;
    private final AssetMapper       assetMapper;

    @PutMapping("/{id}")
    public ResponseEntity<AssetResponse> updateAsset(@PathVariable("id") Long id, @Valid @RequestBody AdminUpdateAssetCommand command) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(id)).getId();
        var asset = adminAssetService.update(kosProfileId, command);
        return ResponseEntity.ok(assetMapper.toDTO(asset));
    }

}
