package com.supergroup.kos.api.config;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.asset.service.AssetService;
import com.supergroup.auth.domain.repository.persistence.DefaultAvatarRepository;
import com.supergroup.kos.dto.config.DefaultAvatarItemResponse;
import com.supergroup.kos.dto.config.DefaultAvatarResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/public/asset/")
public class PublicAssetRestController {

    private final DefaultAvatarRepository defaultAvatarRepository;
    private final AssetService            assetService;

    @GetMapping("/avatars")
    public ResponseEntity<DefaultAvatarResponse> getDefaultAvatar() {
        var listAvatar = defaultAvatarRepository.findAll().stream()
                                                .map(defaultAvatar -> new DefaultAvatarItemResponse(defaultAvatar.getId(),
                                                                                                    assetService.getUrl(defaultAvatar.getAssetId())))
                                                .collect(Collectors.toList());
        return ResponseEntity.ok(new DefaultAvatarResponse(listAvatar));
    }

}
