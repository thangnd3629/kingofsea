package com.supergroup.kos.api.ship;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.kos.building.domain.service.ship.EscortShipGroupConfigService;
import com.supergroup.kos.dto.ship.EscortShipGroupConfigResponse;
import com.supergroup.kos.mapper.EscortShipGroupConfigMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/escort-ship-group-config")
@RequiredArgsConstructor
public class EscortShipGroupConfigRestController {

    private final EscortShipGroupConfigService escortShipGroupConfigService;
    private final EscortShipGroupConfigMapper  escortShipGroupConfigMapper;

    @GetMapping("")
    public ResponseEntity<List<EscortShipGroupConfigResponse>> getEscortShipGroupConfigs() {
        return ResponseEntity.ok(escortShipGroupConfigMapper.toDTOs(escortShipGroupConfigService.getEscortShipGroupConfigs()));
    }

}
