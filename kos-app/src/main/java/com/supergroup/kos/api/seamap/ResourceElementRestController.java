package com.supergroup.kos.api.seamap;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.model.seamap.ResourceIsland;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;
import com.supergroup.kos.dto.seamap.elements.ResourceElementResponse;
import com.supergroup.kos.dto.seamap.mining.ResourceElementDTO;
import com.supergroup.kos.mapper.elements.ElementMapper;
import com.supergroup.kos.mapper.mining.MiningSessionMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@RequestMapping("/v1/seamap/mine")
public class ResourceElementRestController {
    private final SeaElementService seaElementService;
    private final ElementMapper     elementMapper;
    private final MiningSessionMapper miningSessionMapper;

    @GetMapping("")
    public ResponseEntity<?> getMineDetail(@RequestParam Long id) {
        SeaElement seaElement = seaElementService.getElementById(id);
        if (!(seaElement instanceof ResourceIsland)) {
            throw KOSException.of(ErrorCode.RESOURCE_ELEMENT_IS_NOT_FOUND);
        }
        ResourceIsland resourceIsland = (ResourceIsland) seaElement;
        ResourceElementDTO result = new ResourceElementDTO();
        ResourceElementResponse elementResponse = (ResourceElementResponse) elementMapper.map(resourceIsland);
        result.setElementId(elementResponse.getElementId());
        result.setId(elementResponse.getId());
        result.setMiningSession(miningSessionMapper.toResponse(resourceIsland.getMiningSession()));
        return ResponseEntity.ok(result);

    }
}
