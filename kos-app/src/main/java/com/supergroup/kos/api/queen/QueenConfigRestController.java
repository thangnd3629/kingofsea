package com.supergroup.kos.api.queen;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.asset.service.AssetService;
import com.supergroup.core.constant.BaseStatus;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.repository.persistence.queen.QueenConfigDataSource;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.queen.QueenConfigService;
import com.supergroup.kos.dto.queen.QueenConfigResponse;
import com.supergroup.kos.mapper.QueenConfigMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/queen-model")
@RequiredArgsConstructor
public class QueenConfigRestController {

    private final KosProfileService     kosProfileService;
    private final QueenConfigService    queenConfigService;
    private final AssetService          assetService;
    private final QueenConfigDataSource queenConfigDataSource;
    private final QueenConfigMapper     queenConfigMapper;

    @GetMapping("")
    public ResponseEntity<List<QueenConfigResponse>> getQueenModels() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var queenConfigs = queenConfigMapper.toDTOs(queenConfigDataSource.getAll())
                                            .stream()
                                            .map(queenConfigResponse -> {
                                                var isExistQueenModel = queenConfigService.isExist(kosProfile.getId(), queenConfigResponse.getId());
                                                return queenConfigResponse.setIsExist(isExistQueenModel)
                                                                          .setThumbnail(assetService.getUrl(queenConfigResponse.getThumbnail()));
                                            })
                                            // filter queen is deactivate, ignore queen which user own
                                            .filter(e -> e.getStatus().equals(BaseStatus.ACTIVATED))
                                            .collect(Collectors.toList());
        return ResponseEntity.ok(queenConfigs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QueenConfigResponse> getQueenModelById(@PathVariable("id") Long queenModelId) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var queenConfig = queenConfigDataSource.getById(queenModelId);
        var isExist = queenConfigService.isExist(kosProfile.getId(), queenConfig.getId());
        var thumbnail = assetService.getUrl(queenConfig.getThumbnail());
        return ResponseEntity.ok(queenConfigMapper.toDTO(queenConfig).setIsExist(isExist).setThumbnail(thumbnail));
    }

}
