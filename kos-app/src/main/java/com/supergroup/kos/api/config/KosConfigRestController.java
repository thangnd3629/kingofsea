package com.supergroup.kos.api.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.kos.building.domain.model.config.KosFrequencyConfig;
import com.supergroup.kos.building.domain.service.config.KosConfigService;
import com.supergroup.kos.dto.config.KosFrequencyConfigDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user/kos/config")
public class KosConfigRestController {
    private final KosConfigService kosConfigService;

    @GetMapping("/frequency")
    public ResponseEntity<KosFrequencyConfigDTO> getFrequencyConfig() {
        KosFrequencyConfig config = kosConfigService.getFrequencyConfig();
        return ResponseEntity.ok(new KosFrequencyConfigDTO()
                                         .setFrequencyGold(config.getFrequencyGold())
                                         .setFrequencyPeople(config.getFrequencyPeople())
                                         .setFrequencyStone(config.getFrequencyStone())
                                         .setFrequencyWood(config.getFrequencyWood()));
    }

}
