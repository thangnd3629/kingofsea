package com.supergroup.kos.api.weapon;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.asset.service.AssetService;
import com.supergroup.kos.building.domain.command.CreateWeaponSetCommand;
import com.supergroup.kos.building.domain.command.GetWeaponSetCommand;
import com.supergroup.kos.building.domain.command.UpgradeWeaponSetCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.model.config.WeaponSetConfig;
import com.supergroup.kos.building.domain.model.weapon.Weapon;
import com.supergroup.kos.building.domain.model.weapon.WeaponSet;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.weapon.WeaponSetConfigService;
import com.supergroup.kos.building.domain.service.weapon.WeaponSetService;
import com.supergroup.kos.dto.PageResponse;
import com.supergroup.kos.dto.weapon.WeaponSetConfigResponse;
import com.supergroup.kos.dto.weapon.WeaponSetMergeResponse;
import com.supergroup.kos.dto.weapon.WeaponSetResponse;
import com.supergroup.kos.mapper.WeaponSetMapper;
import com.supergroup.kos.mapper.WeaponSetMergeMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/weapon-set")
@RequiredArgsConstructor
public class WeaponSetRestController {

    private final KosProfileService    kosProfileService;
    private final WeaponSetService     weaponSetService;
    private final AssetService         assetService;
    private final WeaponSetMapper      weaponSetMapper;
    private final WeaponSetMergeMapper weaponSetMergeMapper;
    private final WeaponSetConfigService  weaponSetConfigService;

    @GetMapping("")
    public ResponseEntity<PageResponse<WeaponSetResponse>> getWeaponSets(Pageable pageable) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        var rs = weaponSetService.getWeaponSets(new GetWeaponSetCommand().setKosProfileId(kosProfileId), pageable);
        var data = rs.getContent().stream().map(weaponSet -> {
            var thumbnail = assetService.getUrl(weaponSet.getWeaponSetConfig().getThumbnail());
            var res = weaponSetMapper.toDTO(weaponSet);
            res.getModel().setThumbnail(thumbnail);
            return res;
        }).collect(Collectors.toList());
        var response = new PageResponse<WeaponSetResponse>().setTotal(rs.getTotalElements()).setData(data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WeaponSetResponse> getWeaponSetById(@PathVariable("id") Long weaponSetId) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        var weaponSet = weaponSetService.getWeaponSetById(new GetWeaponSetCommand().setKosProfileId(kosProfileId).setWeaponSetId(weaponSetId));
        var thumbnail = assetService.getUrl(weaponSet.getWeaponSetConfig().getThumbnail());
        weaponSet.getWeaponSetConfig().setThumbnail(thumbnail);
        return ResponseEntity.ok(weaponSetMapper.toDTO(weaponSet));
    }

    @PostMapping("")
    public ResponseEntity<WeaponSetMergeResponse> createWeaponSet(@Valid @RequestBody CreateWeaponSetCommand command) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        var response = weaponSetService.createWeaponSet(command.setKosProfileId(kosProfileId));
        if (response instanceof WeaponSet) {
            var thumbnail = assetService.getUrl(((WeaponSet) response).getWeaponSetConfig().getThumbnail());
            var res = weaponSetMergeMapper.toDTO((WeaponSet) response);
            res.setIsSuccess(true);
            res.getWeaponSet().getModel().setThumbnail(thumbnail);
            return ResponseEntity.ok(res);
        } else {
            var thumbnail = assetService.getUrl(((Weapon) response).getWeaponConfig().getThumbnail());
            var res = weaponSetMergeMapper.toDTO((Weapon) response);
            res.setIsSuccess(false);
            res.getWeaponLost().getModel().setThumbnail(thumbnail);
            return ResponseEntity.ok(res);
        }
    }

    @PutMapping("/{id}/upgrade")
    public ResponseEntity<?> upgrade(@PathVariable("id") Long weaponSetId) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId())).getId();
        weaponSetService.upgrade(new UpgradeWeaponSetCommand().setKosProfileId(kosProfileId).setWeaponSetId(weaponSetId));
        return ResponseEntity.accepted().build();
    }
    @GetMapping("/config")
    public ResponseEntity<?> getAllConfig(){
        List<WeaponSetConfig> configs = weaponSetConfigService.getAllConfigModel();
        Collection<WeaponSetConfigResponse> results =weaponSetMapper.toDTOs(configs);
        results = results.stream().map(result -> result.setThumbnail(assetService.getUrl(result.getThumbnail()))).collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

}
