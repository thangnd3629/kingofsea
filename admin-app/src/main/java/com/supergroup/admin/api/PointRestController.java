package com.supergroup.admin.api;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.admin.domain.command.AdminUpdatePointCommand;
import com.supergroup.admin.domain.service.AdminPointService;
import com.supergroup.admin.dto.PointResponse;
import com.supergroup.admin.mapper.PointMapper;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/point")
@RequiredArgsConstructor
public class PointRestController {

    private final AdminPointService adminPointService;
    private final KosProfileService kosProfileService;

    private final PointMapper pointMapper;

    @PutMapping("/{id}")
    public ResponseEntity<PointResponse> updateAsset(@PathVariable("id") Long id, @Valid @RequestBody AdminUpdatePointCommand command) {
        var kosProfileId = kosProfileService.getKosProfile(new UserCommand().setUserId(id)).getId();
        var asset = adminPointService.update(kosProfileId, command);
        return ResponseEntity.ok(pointMapper.toDTO(asset));
    }

}
