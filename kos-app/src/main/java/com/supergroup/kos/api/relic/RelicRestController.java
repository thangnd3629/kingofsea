package com.supergroup.kos.api.relic;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.asset.service.AssetService;
import com.supergroup.kos.building.domain.command.GetRelicsCommand;
import com.supergroup.kos.building.domain.command.ListingRelicCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.model.config.RelicConfig;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.relic.RelicService;
import com.supergroup.kos.dto.relic.ListingRelicRequest;
import com.supergroup.kos.dto.relic.RelicConfigResponse;
import com.supergroup.kos.dto.relic.RelicResponse;
import com.supergroup.kos.mapper.RelicConfigMapper;
import com.supergroup.kos.mapper.RelicMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/relic")
@RequiredArgsConstructor
public class RelicRestController {
    private final KosProfileService kosProfileService;
    private final RelicService relicService;
    private final AssetService assetService;
    private final RelicMapper relicMapper;
    private final RelicConfigMapper relicConfigMapper;

    @GetMapping("")
    public ResponseEntity<?> getRelics(@RequestParam(value = "isListing", required = false) Boolean isListing) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var getRelicsCommand = new GetRelicsCommand().setIsListing(isListing).setKosProfileId(kosProfile.getId());
        var relics = relicService.getRelics(getRelicsCommand);
        var data = relics.stream().map(relic -> {
            var thumbnail = assetService.getUrl(relic.getRelicConfig().getThumbnail());
            var res = relicMapper.toDTO(relic);
            res.getModel().setThumbnail(thumbnail);
            return res;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("data", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RelicResponse> getRelicById(@PathVariable Long id) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var relic = relicService.getRelicByIdAndKosProfileId(id, kosProfile.getId());
        var thumbnail = assetService.getUrl(relic.getRelicConfig().getThumbnail());
        relic.setRelicConfig(relic.getRelicConfig().setThumbnail(thumbnail));
        return ResponseEntity.ok(relicMapper.toDTO(relic));
    }

    // Listing relic
    @PutMapping("/{relicId}")
    public ResponseEntity<?> listingRelic(@PathVariable Long relicId, @Valid @RequestBody ListingRelicRequest request) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var listingRelicCommand = new ListingRelicCommand()
                .setRelicId(relicId)
                .setIsListing(request.getIsListing())
                .setKosProfileId(kosProfile.getId());
        relicService.listingRelic(listingRelicCommand);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/config")
    public ResponseEntity<?> getAll(Pageable pageable) {
        List<RelicConfig> relicConfigs = relicService.getAllConfig(pageable);
        List<RelicConfigResponse> response = relicConfigMapper.toDTOS(relicConfigs).stream().map(relicConfigResponse -> relicConfigResponse.setThumbnail(assetService.getUrl(relicConfigResponse.getThumbnail()))
                ).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
