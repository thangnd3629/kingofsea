package com.supergroup.kos.api.ship;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.kos.building.domain.constant.EscortShipGroupLevel;
import com.supergroup.kos.building.domain.constant.EscortShipGroupName;
import com.supergroup.kos.building.domain.service.ship.EscortShipGroupLevelConfigService;
import com.supergroup.kos.dto.ship.EscortShipGroupLevelConfigResponse;
import com.supergroup.kos.mapper.EscortShipGroupLevelConfigMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/escort-ship-group-level-config")
@RequiredArgsConstructor
public class EscortShipGroupLevelConfigRestController {
    private final EscortShipGroupLevelConfigService escortShipGroupLevelConfigService;
    private final EscortShipGroupLevelConfigMapper  escortShipGroupLevelConfigMapper;

    @GetMapping("")
    public ResponseEntity<List<EscortShipGroupLevelConfigResponse>> getEscortShipGroupLevelConfigs(
            @RequestParam(name = "quality", required = false) EscortShipGroupLevel level,
            @RequestParam(name = "group", required = false) EscortShipGroupName shipGroupName
                                                                                                  ) {
        var levelConfigs = escortShipGroupLevelConfigService.getEscortShipGroupLevelConfigsFilter(level, shipGroupName);
        return ResponseEntity.ok(escortShipGroupLevelConfigMapper.toDTOs(levelConfigs));
    }

}
