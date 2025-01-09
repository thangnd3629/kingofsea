package com.supergroup.kos.api.data;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.asset.service.AssetService;
import com.supergroup.kos.building.domain.service.seamap.ElementsConfigService;
import com.supergroup.kos.dto.data.DataStaticResponse;
import com.supergroup.kos.dto.data.ElementsConfigResponse;
import com.supergroup.kos.mapper.seamap.ElementsConfigMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/kos/data-static")
public class DataStaticRestController {
    private final ElementsConfigService elementsConfigService;
    private final ElementsConfigMapper  elementsConfigMapper;
    private final AssetService          assetService;

    @GetMapping
    public ResponseEntity<DataStaticResponse> getDataStatic() {
        DataStaticResponse response = new DataStaticResponse();
        List<ElementsConfigResponse> elementsConfigResponseList = elementsConfigMapper.maps(elementsConfigService.getAllElementsConfig());
        for (ElementsConfigResponse elementsConfigResponse : elementsConfigResponseList) {
            elementsConfigResponse.setThumbnail(assetService.getUrl(elementsConfigResponse.getThumbnail()));
        }
        return ResponseEntity.ok(response.setElements(elementsConfigResponseList));
    }
}
