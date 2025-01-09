package com.supergroup.admin.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.admin.domain.command.GetElementInAreaCommand;
import com.supergroup.admin.domain.service.AdminSeaElementService;
import com.supergroup.admin.domain.service.AdminSeaMapRefreshTransactionService;
import com.supergroup.admin.mapper.SeaMapRefreshTransactionMapper;
import com.supergroup.admin.mapper.elements.ElementMapper;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.SaveOrUpdateElementCommand;
import com.supergroup.kos.building.domain.model.seamap.RefreshNpcAndMineResult;
import com.supergroup.kos.building.domain.model.seamap.ResourceIsland;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementRepository;
import com.supergroup.kos.building.domain.service.seamap.MapService;
import com.supergroup.kos.building.domain.service.seamap.RefreshNpcAndMineService;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/admin/seamap")
@RequiredArgsConstructor
@Slf4j
public class SeaMapRestController {
    private final RefreshNpcAndMineService             npcElementRefreshService;
    private final AdminSeaElementService               adminSeaElementService;
    private final ElementMapper                        elementMapper;
    private final SeaMapRefreshTransactionMapper       seaMapRefreshTransactionMapper;
    private final AdminSeaMapRefreshTransactionService adminSeaMapRefreshTransactionService;
    private final MapService                           mapService;
    private final SeaElementRepository<SeaElement>     elementPersistenceRepository;
    private final SeaElementService                    seaElementService;

    @GetMapping("area")
    public ResponseEntity<?> getMap(@RequestParam(name = "x") Long x,
                                    @RequestParam(name = "y") Long y,
                                    @RequestParam(name = "width") Long width,
                                    @RequestParam(name = "height") Long height) {
        var elements = adminSeaElementService.getElements(new GetElementInAreaCommand().setX(x)
                                                                                       .setY(y)
                                                                                       .setWidth(width)
                                                                                       .setHeight(height));
        return ResponseEntity.ok(elementMapper.maps(elements));
    }

    @PostMapping("/refresh-npc-mine")
    public ResponseEntity<?> refreshNpcAndMine() {
        RefreshNpcAndMineResult result = npcElementRefreshService.refreshNpcAndMine();
//        mapService.syncUpMapFromDatabaseToCache();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/hidden-resource_island/{resource_island_id}")
    public ResponseEntity<?> hiddenResourceIsland(@PathVariable Long resource_island_id) {
        SeaElement seaElement = seaElementService.getElementById(resource_island_id);
        if (seaElement instanceof ResourceIsland) {
            // check mining todo
            seaElement.setActive(false);
            mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(seaElement));
        } else {
            throw KOSException.of(ErrorCode.BAD_REQUEST_ERROR);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/refresh-history")
    public ResponseEntity<?> refreshHistory() {
        return ResponseEntity.ok(seaMapRefreshTransactionMapper.toResponses(adminSeaMapRefreshTransactionService.findAll()));
    }

    @GetMapping("/refresh-element")
    public ResponseEntity<?> refreshElement() {
        var list = elementPersistenceRepository.findAll();
        var done = 0;
        var size = list.size();
        for (SeaElement seaElement : list) {
            try {
                seaElementService.saveToCache(seaElement);
                done++;
                log.info("Refresh element {}/{}", done, size);
            } catch (Exception exception) {
                exception.printStackTrace();
                // ignore
            }
        }
        return ResponseEntity.ok().build();
    }
}
