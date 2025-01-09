package com.supergroup.admin.domain.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.SaveOrUpdateElementCommand;
import com.supergroup.kos.building.domain.command.WithdrawActivityCommand;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.model.config.seamap.ResourceIslandConfig;
import com.supergroup.kos.building.domain.model.seamap.ResourceIsland;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementConfigRepository;
import com.supergroup.kos.building.domain.service.seamap.ElementsConfigService;
import com.supergroup.kos.building.domain.service.seamap.MapService;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AdminMiningService {
    private final SeaElementService                                seaElementService;
    private final MapService                                       mapService;
    private final SeaElementConfigRepository<ResourceIslandConfig> resourceConfigRepo;
    private final ElementsConfigService                            elementsConfigService;
    private final SeaActivityService                               seaActivityService;

    public void resetMine(Long mineId) {
        SeaElement seaElement = seaElementService.getElementById(mineId);
        if (!(seaElement instanceof ResourceIsland)) {
            throw KOSException.of(ErrorCode.ELEMENT_NOT_FOUND);
        }
        ResourceIsland resourceIsland = (ResourceIsland) seaElement;
        resourceIsland.setMined(0D);
        mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(resourceIsland));
    }

    public void changeMaxCap(Long mineId, Double baseCap) {
        SeaElement mine = seaElementService.getElementById(mineId);
        if (!(mine instanceof ResourceIsland)) {
            throw KOSException.of(ErrorCode.ELEMENT_NOT_FOUND);
        }
        List<SeaElement> seaElements = seaElementService.findSeaElementByTypeFromDatabase(List.of(SeaElementType.RESOURCE));
        for (SeaElement element : seaElements) {
            if (element instanceof ResourceIsland) {
                ResourceIsland resourceIsland = (ResourceIsland) element;
                if (Objects.nonNull(resourceIsland.getMiningSession())) {
                    seaActivityService.withdraw(new WithdrawActivityCommand().setId(resourceIsland.getMiningSession().getSeaActivity().getId()));
                }
                resourceIsland.setMined(0D);
                mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(resourceIsland));
            }
        }
        ResourceIslandConfig resourceIslandConfig = resourceConfigRepo.findResourceIslandConfigById(mine.getSeaElementConfig().getId()).orElseThrow(
                () -> KOSException.of(ErrorCode.SERVER_ERROR));
        resourceIslandConfig.setResourceCapacity(baseCap);
        resourceConfigRepo.save(resourceIslandConfig);
        elementsConfigService.deleteConfigCache();

    }

}
