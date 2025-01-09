package com.supergroup.kos.api.weapon;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.asset.service.AssetService;
import com.supergroup.core.constant.BaseStatus;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.technology.UserTechnologyService;
import com.supergroup.kos.building.domain.service.weapon.WeaponService;
import com.supergroup.kos.building.domain.service.weapon.WeaponSetConfigService;
import com.supergroup.kos.dto.technology.TechnologyDTO;
import com.supergroup.kos.dto.weapon.MergeWeaponSetRequirement;
import com.supergroup.kos.dto.weapon.WeaponSetConfigResponse;
import com.supergroup.kos.mapper.WeaponConfigMapper;
import com.supergroup.kos.mapper.WeaponSetConfigMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/weapon-set-model")
@RequiredArgsConstructor
public class WeaponSetConfigRestController {
    private final KosProfileService      kosProfileService;
    private final WeaponSetConfigService weaponSetConfigService;
    private final WeaponService          weaponService;
    private final UserTechnologyService  userTechnologyService;
    private final AssetService           assetService;
    private final WeaponSetConfigMapper  weaponSetConfigMapper;
    private final WeaponConfigMapper     weaponConfigMapper;

    @GetMapping("")
    public ResponseEntity<List<WeaponSetConfigResponse>> getWeaponSetModels() {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        // @formatter:off
        var responses = weaponSetConfigService.getWeaponSetConfigs(kosProfileId)
                                              .stream()
                                              .filter(e -> e.getStatus().equals(BaseStatus.ACTIVATED))
                                              .map(weaponSetConfig -> {
                                                  var technology = userTechnologyService.findByKosProfileIdAndTechnologyCode(weaponSetConfig.getTechnologyRequirement(), kosProfileId);
                                                  var requirement = new MergeWeaponSetRequirement()
                                                          .setGold(weaponSetConfig.getGold())
                                                          .setTechnology(new TechnologyDTO().setCode(technology.getTechnology().getCode())
                                                                                            .setType(technology.getTechnology().getTechnologyType())
                                                                                            .setName(technology.getTechnology().getName())
                                                                                            .setIsResearched(technology.getIsResearched()));
                                                  var thumbnail = assetService.getUrl(weaponSetConfig.getThumbnail());
                                                  var res = weaponSetConfigMapper.toDTO(weaponSetConfig);
                                                  res.setRequirement(requirement)
                                                     .setThumbnail(thumbnail);
                                                  return res;
                                              })
                                              .collect(Collectors.toList());
        // @formatter:on
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WeaponSetConfigResponse> getWeaponSetModelById(@PathVariable Long id) {
        // formatter:off
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        var weaponSetConfig = weaponSetConfigService.getWeaponSetConfigById(kosProfileId, id);
        var weaponConfigResponses = weaponSetConfig.getWeaponConfigs()
                                                   .stream()
                                                   .filter(w -> w.getStatus().equals(BaseStatus.ACTIVATED))
                                                   .map(wc -> {
                                                       var res = weaponConfigMapper.toDTO(wc);
                                                       var weapons = weaponService.getByWeaponConfigIdAndKosProfileId(wc.getId(), kosProfileId);
                                                       var equippedOnShips = weapons.stream()
                                                                                    .filter(weapon -> Objects.nonNull(weapon.getMotherShip()))
                                                                                    .map(w -> w.getMotherShip().getId())
                                                                                    .collect(Collectors.toList());
                                                       var thumbnail = assetService.getUrl(wc.getThumbnail());
                                                       res.setQualityExist((long) weapons.size())
                                                          .setEquippedOnShips(equippedOnShips)
                                                          .setThumbnail(thumbnail);
                                                       return res;
                                                   })
                                                   .collect(Collectors.toList());
        var technology = userTechnologyService.findByKosProfileIdAndTechnologyCode(weaponSetConfig.getTechnologyRequirement(), kosProfileId);
        var requirement = new MergeWeaponSetRequirement()
                .setGold(weaponSetConfig.getGold())
                .setTechnology(new TechnologyDTO().setCode(technology.getTechnology().getCode())
                                                  .setType(technology.getTechnology().getTechnologyType())
                                                  .setName(technology.getTechnology().getName())
                                                  .setIsResearched(technology.getIsResearched()));
        var thumbnail = assetService.getUrl(weaponSetConfig.getThumbnail());
        weaponSetConfig.setThumbnail(thumbnail);
        var response = weaponSetConfigMapper.toDTO(weaponSetConfig)
                                            .setWeaponModels(weaponConfigResponses)
                                            .setRequirement(requirement);
        return ResponseEntity.ok(response);
        // formatter:on
    }

}
