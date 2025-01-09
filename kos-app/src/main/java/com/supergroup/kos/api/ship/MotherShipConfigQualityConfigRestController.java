package com.supergroup.kos.api.ship;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.asset.service.AssetService;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.repository.persistence.ship.MotherShipConfigQualityConfigDataSource;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.technology.UserTechnologyService;
import com.supergroup.kos.dto.ship.MotherShipConfigQualityConfigResponse;
import com.supergroup.kos.mapper.MotherShipConfigQualityConfigMapper;
import com.supergroup.kos.mapper.TechnologyRequirementMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/mother-ship-model")
@RequiredArgsConstructor
public class MotherShipConfigQualityConfigRestController {
    private final AssetService                            assetService;
    private final UserTechnologyService                   userTechnologyService;
    private final KosProfileService                       kosProfileService;
    private final MotherShipConfigQualityConfigMapper     motherShipConfigQualityConfigMapper;
    private final TechnologyRequirementMapper             technologyRequirementMapper;
    private final MotherShipConfigQualityConfigDataSource motherShipConfigQualityConfigDataSource;

    @GetMapping("")
    public ResponseEntity<List<MotherShipConfigQualityConfigResponse>> getMotherShipModels() {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        // get all activate mother ship model
        var motherShipModels = motherShipConfigQualityConfigDataSource.getAll();
        var response = motherShipModels.stream().map(motherShipModel -> {
                                           var thumbnail = assetService.getUrl(motherShipModel.getMotherShipConfig().getThumbnail());
                                           var res = motherShipConfigQualityConfigMapper.toDTO(motherShipModel);
                                           var technologyCode = motherShipModel.getMotherShipConfig().getTechnologyRequirement();
                                           if (Objects.nonNull(technologyCode)) {
                                               var ut = userTechnologyService.findByKosProfileIdAndTechnologyCode(technologyCode, kosProfileId);
                                               var technologyRequirement = technologyRequirementMapper.toDTO(ut);
                                               res.getBuyRequirement().setTechnology(technologyRequirement);
                                           }
                                           res.getModel().setThumbnail(thumbnail);
                                           return res;
                                       })
                                       .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MotherShipConfigQualityConfigResponse> getMotherShipModel(@PathVariable Long id) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        var config = motherShipConfigQualityConfigDataSource.getById(id);
        var thumbnail = assetService.getUrl(config.getMotherShipConfig().getThumbnail());
        var response = motherShipConfigQualityConfigMapper.toDTO(config);
        var technologyCode = config.getMotherShipConfig().getTechnologyRequirement();
        if (Objects.nonNull(technologyCode)) {
            var ut = userTechnologyService.findByKosProfileIdAndTechnologyCode(technologyCode, kosProfileId);
            var technologyRequirement = technologyRequirementMapper.toDTO(ut);
            response.getBuyRequirement().setTechnology(technologyRequirement);
        }
        response.getModel().setThumbnail(thumbnail);
        return ResponseEntity.ok(response);
    }
}
