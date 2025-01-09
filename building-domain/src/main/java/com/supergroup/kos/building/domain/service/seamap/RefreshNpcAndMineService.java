package com.supergroup.kos.building.domain.service.seamap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.SaveOrUpdateElementCommand;
import com.supergroup.kos.building.domain.command.WithdrawActivityCommand;
import com.supergroup.kos.building.domain.constant.seamap.BossSeaStatus;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.constant.seamap.ZoneSeaType;
import com.supergroup.kos.building.domain.model.config.seamap.ElementAccording;
import com.supergroup.kos.building.domain.model.config.seamap.ElementAccordingZone;
import com.supergroup.kos.building.domain.model.config.seamap.ResourceIslandConfig;
import com.supergroup.kos.building.domain.model.config.seamap.SeaElementConfig;
import com.supergroup.kos.building.domain.model.config.seamap.SeaMapConfig;
import com.supergroup.kos.building.domain.model.config.seamap.SizeZoneSeaConfig;
import com.supergroup.kos.building.domain.model.config.seamap.ZoneSeaConfig;
import com.supergroup.kos.building.domain.model.seamap.BossSea;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.DeleteNpcAndMineBeforeRefreshResult;
import com.supergroup.kos.building.domain.model.seamap.ElementRefresh;
import com.supergroup.kos.building.domain.model.seamap.RefreshNpcAndMineResult;
import com.supergroup.kos.building.domain.model.seamap.ResourceIsland;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.repository.persistence.seamap.UserBaseRepository;
import com.supergroup.kos.building.domain.service.config.KosConfigService;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;
import com.supergroup.kos.building.domain.utils.SeaMapCoordinatesUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class RefreshNpcAndMineService {
    private final SeaElementService     seaElementService;
    private final UserBaseRepository    userBaseRepository;
    private final ElementsConfigService elementsConfigService;
    private final KosConfigService      kosConfigService;
    private final SeaActivityService    seaActivityService;
    private final MapService            mapService;
    private final Integer               RADIUS = 25;

    public RefreshNpcAndMineResult refreshNpcAndMine() {
        RefreshNpcAndMineResult result = new RefreshNpcAndMineResult();
        // delete old npc and mine before refresh
        log.info("Delete old npc and mine before refresh");
        var resetDelete = deleteNpcAndMineWhenRefresh();
        result.setTotalElementDeleted(resetDelete.getTotalDelete());
        result.setTotalElementNotDeleted(resetDelete.getElementNotDelete().size());

        SeaMapConfig seaMapConfig = kosConfigService.getSeaMapConfig();
        List<UserBase> listUserBaseActive = userBaseRepository.findByActive(true);
        Map<String, String> coordinatesFree = new HashMap<>(getAllCoordinatesFree());

        // remove coordinate with near by user base from free coordinates
        log.info("Remove coordinate with near by user base from free coordinates");
        for (UserBase userBase : listUserBaseActive) {
            Coordinates coordinates = userBase.getCoordinates();
            coordinatesFree.remove(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX(), coordinates.getY()));
            coordinatesFree.remove(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX(), coordinates.getY() + 1));
            coordinatesFree.remove(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX() + 1, coordinates.getY()));
            coordinatesFree.remove(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX() + 1, coordinates.getY() + 1));
        }

        log.info("Remove coordinate with near by sea element not delete from free coordinates");
        for(ElementRefresh elementRefresh: resetDelete.getElementNotDelete()) {
            coordinatesFree.remove(SeaMapCoordinatesUtils.toStringCoordinates(elementRefresh.getX(), elementRefresh.getY()));
        }

        // create npc and mine by base
        log.info("Create npc and mine by base");
        List<SeaElement> newAccordingBaseUserSeaElement = new ArrayList<>();
        for (UserBase userBase : listUserBaseActive) {
            createElementAccordingBaseUser(newAccordingBaseUserSeaElement, userBase, seaMapConfig, coordinatesFree);
        }

        // create npc and mine by zone
        log.info("Create npc and mine by zone");
        List<SeaElement> seaElementAccordingZone = createSeaElementAccordingZone(coordinatesFree, seaMapConfig.getElementAccordingZones());

        // save to database
        log.info("Save new npc and mine to database");
        saveElementsToDatabase(newAccordingBaseUserSeaElement);
        saveElementsToDatabase(seaElementAccordingZone);

        // save to cache
        log.info("Save new npc and mine to cache");
        updateNpcAndMineFromDatabaseToCache();

        result.setTotalElementAccordingBaseCreated(newAccordingBaseUserSeaElement.size());
        result.setTotalElementAccordingZoneSeaCreated(seaElementAccordingZone.size());
        return result;
    }

    @Transactional
    public  void saveElementsToDatabase(List<SeaElement> elements) {
        seaElementService.saveAllToDatabase(elements);
    }

    @Transactional
    private DeleteNpcAndMineBeforeRefreshResult deleteNpcAndMineWhenRefresh() {
        // get all refreshable element
        List<ElementRefresh> listElement = seaElementService.getElementsRefresh();
        DeleteNpcAndMineBeforeRefreshResult result = new DeleteNpcAndMineBeforeRefreshResult();
        List<Long> elementsDelete = new ArrayList<>();
        List<ElementRefresh> elementsNotDelete = new ArrayList<>();
        for(ElementRefresh elementRefresh: listElement) {
            if(elementRefresh.getBattleId() == null) {
                if(Objects.equals(elementRefresh.getType(), SeaElementType.RESOURCE.name())) {
                    if(elementRefresh.getMiningSessionId() != null && elementRefresh.getSeaActivityId() != null) {
                        seaActivityService.withdraw(new WithdrawActivityCommand().setId(elementRefresh.getSeaActivityId()));
                    }
                }
                elementsDelete.add(elementRefresh.getId());
            } else {
                elementsNotDelete.add(elementRefresh);
            }
        }

        // delete Element Can Delete
        for(Long seaElementDeleteId: elementsDelete) {
            seaElementService.deleteById(seaElementDeleteId);
        }

        // update element not delete
        List<Long> idsElementNotDelete = elementsNotDelete.stream().map(ElementRefresh::getId).collect(Collectors.toList());
        seaElementService.updateDeletedElement(idsElementNotDelete, true);

        result.setTotalDelete(elementsDelete.size());
        result.setElementNotDelete(elementsNotDelete);
        return result;
    }

    private List<SeaElement> createSeaElementAccordingZone(Map<String, String> coordinatesFree, List<ElementAccordingZone> list) {
        List<SeaElementConfig> zoneOneElementConfig = new ArrayList<>();
        List<SeaElementConfig> zoneTwoElementConfig = new ArrayList<>();
        List<SeaElementConfig> zoneThreeElementConfig = new ArrayList<>();
        for (ElementAccordingZone elementAccordingZone : list) {
            switch (elementAccordingZone.getZoneSeaType()) {
                case ZONE_ONE:
                    zoneOneElementConfig = getListElementConfigFromConfig(elementAccordingZone.getElements());
                    break;
                case ZONE_TWO:
                    zoneTwoElementConfig = getListElementConfigFromConfig(elementAccordingZone.getElements());
                    break;
                case ZONE_THREE:
                    zoneThreeElementConfig = getListElementConfigFromConfig(elementAccordingZone.getElements());
                    break;
                default:
                    throw KOSException.of(ErrorCode.BAD_REQUEST_ERROR);
            }
        }
        Map<String, String> zoneOne = new HashMap<>();
        Map<String, String> zoneTwo = new HashMap<>();
        Map<String, String> zoneThree = new HashMap<>();
        ZoneSeaConfig zoneSeaConfig = kosConfigService.getZoneSeaConfig();
        var keySet = coordinatesFree.keySet();
        for (String coordinatesString : keySet) {
            Coordinates coordinates = SeaMapCoordinatesUtils.stringToCoordinates(coordinatesString);
            switch (getZoneSeaType(coordinates, zoneSeaConfig.getSizeZoneSea())) {
                case ZONE_ONE:
                    zoneOne.put(coordinatesString, null);
                    break;
                case ZONE_TWO:
                    zoneTwo.put(coordinatesString, null);
                    break;
                case ZONE_THREE:
                    zoneThree.put(coordinatesString, null);
                    break;
                default:
                    throw KOSException.of(ErrorCode.BAD_REQUEST_ERROR);
            }
        }
        List<SeaElement> listSeaElement = new ArrayList<>();
        createElementInZoneSea(listSeaElement, zoneOne, zoneOneElementConfig);
        createElementInZoneSea(listSeaElement, zoneTwo, zoneTwoElementConfig);
        createElementInZoneSea(listSeaElement, zoneThree, zoneThreeElementConfig);
        return listSeaElement;

    }

    private ZoneSeaType getZoneSeaType(Coordinates coordinates, List<SizeZoneSeaConfig> sizeZoneSeaConfigs) {
        for (SizeZoneSeaConfig config : sizeZoneSeaConfigs) {
            Long radius = config.getRadius();
            if (coordinates.getX() <= radius && coordinates.getX() >= (-radius) && coordinates.getY() <= radius && coordinates.getY() >= (-radius)) {
                return config.getType();
            }
        }
        throw KOSException.of(ErrorCode.BAD_REQUEST_ERROR);
    }

    private SeaElement createSeaElement(SeaElementConfig elementsConfig, Coordinates coordinates, Long dependentElementId) {
        switch (elementsConfig.getType()) {
            case RESOURCE:
                var resourceIsland = new ResourceIsland();
                resourceIsland.setMined(0D)
                              .setResourceType(((ResourceIslandConfig) elementsConfig).getResourceType())
                              .setActive(true)
                              .setIsRefreshable(true)
                              .setDependentElementId(dependentElementId)
                              .setSeaElementConfig(elementsConfig)
                              .setCoordinates(coordinates);
                return resourceIsland;
            case BOSS:
                var bossSea = new BossSea();
                bossSea.setStatus(BossSeaStatus.NORMAL)
                       .setHpLost(0L)
                       .setCoordinate(coordinates)
                       .setDependentElementId(dependentElementId)
                       .setActive(true)
                       .setIsRefreshable(true)
                       .setSeaElementConfig(elementsConfig);
                return bossSea;
            default:
                throw KOSException.of(ErrorCode.BAD_REQUEST_ERROR);
        }
    }

    @Transactional
    private void updateNpcAndMineFromDatabaseToCache() {
        // delete all npc and mine on cache
        var listTypes = List.of(SeaElementType.BOSS, SeaElementType.RESOURCE);
        seaElementService.deleteSeaElementByTypeFromCache(listTypes);
        // get newest npc and mine and save to cache
        var elements = seaElementService.findSeaElementByTypeFromDatabase(listTypes);
        for (SeaElement element : elements) {
            seaElementService.saveToCache(element);
        }
    }

    private List<SeaElement> createElementInZoneSea(List<SeaElement> seaElementList, Map<String, String> map,
                                                    List<SeaElementConfig> elementsCreates) {
        var coordinatesFree = new ArrayList<>(map.keySet());
        var sizeCoordinatesFree = coordinatesFree.size();
        var quantityElementCreate = elementsCreates.size();
        if (sizeCoordinatesFree > quantityElementCreate) {
            Set<Integer> setIndex = new HashSet<>();
            for (SeaElementConfig seaElementConfig : elementsCreates) {
                int index;
                do {
                    Random random = new Random();
                    index = random.nextInt(sizeCoordinatesFree);
                } while (setIndex.contains(index));
                seaElementList.add(createSeaElement(seaElementConfig,
                                                    SeaMapCoordinatesUtils.stringToCoordinates(coordinatesFree.get(index)),
                                                    null));
                setIndex.add(index);
            }
        } else {
            int counter = 0;
            for (var key : coordinatesFree) {
                seaElementList.add(createSeaElement(elementsCreates.get(counter),
                                                    SeaMapCoordinatesUtils.stringToCoordinates(key),
                                                    null));
                counter++;
            }

        }
        return seaElementList;
    }

    private List<SeaElementConfig> getListElementConfigFromConfig(List<ElementAccording> elementAccordingList) {
        Map<Long, SeaElementConfig> mapConfig = elementsConfigService.getMapElementConfig();
        List<SeaElementConfig> response = new ArrayList<>();
        for (ElementAccording elementAccording : elementAccordingList) {
            for (int i = 0; i < elementAccording.getQuantity(); i++) {
                response.add(mapConfig.get(elementAccording.getElementConfigId()));
            }
        }
        return response;
    }

    private void createElementAccordingBaseUser(List<SeaElement> seaElementAccordingBaseUserNew,
                                                UserBase userBase,
                                                SeaMapConfig seaMapConfig,
                                                Map<String, String> mapCoordinatesFree) {

        var elementAccordingUserBase = getListElementConfigFromConfig(seaMapConfig.getElementAccordingBaseUser());

        // find placeable coordinate
        List<Coordinates> placeableCoordinatesElement = new ArrayList<>();
        Coordinates coordinatesBase = userBase.getCoordinates();
        for (int width = -RADIUS; width < RADIUS; width++) {
            for (int height = -RADIUS; height < RADIUS; height++) {
                var coordinate = new Coordinates(coordinatesBase.getX() + width,
                                                 coordinatesBase.getY() + height);
                String coordinatesString = SeaMapCoordinatesUtils.toStringCoordinates(coordinate.getX(), coordinate.getY());
                if (mapCoordinatesFree.containsKey(coordinatesString)) {
                    placeableCoordinatesElement.add(new Coordinates(coordinatesBase.getX() + width,
                                                                    coordinatesBase.getY() + height));
                }
            }
        }

        if (placeableCoordinatesElement.size() <= elementAccordingUserBase.size()) {
            var counter = 0;
            for (Coordinates coordinates : placeableCoordinatesElement) {
                seaElementAccordingBaseUserNew.add(createSeaElement(elementAccordingUserBase.get(counter), coordinates, userBase.getId()));
                mapCoordinatesFree.remove(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX(), coordinates.getY()));
                counter++;
            }
        } else {
            Set<Integer> setIndex = new HashSet<>();
            var sizeCoordinatesFree = placeableCoordinatesElement.size();
            for (SeaElementConfig seaElementConfig : elementAccordingUserBase) {
                int index;
                do {
                    Random random = new Random();
                    index = random.nextInt(sizeCoordinatesFree);
                } while (setIndex.contains(index));
                var coordinates = placeableCoordinatesElement.get(index);
                seaElementAccordingBaseUserNew.add(createSeaElement(seaElementConfig,
                                                                    coordinates,
                                                                    userBase.getId()));
                setIndex.add(index);
                mapCoordinatesFree.remove(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX(), coordinates.getY()));
            }
        }

    }

    private Map<String, String> getAllCoordinatesFree() {
        var coordinatesFree = new HashMap<String, String>();
        var zoneSeaConfig = kosConfigService.getZoneSeaConfig();
        for (var x = -zoneSeaConfig.getRadius(); x <= zoneSeaConfig.getRadius() - 1; x++) {
            for (var y = -zoneSeaConfig.getRadius(); y <= zoneSeaConfig.getRadius() - 1; y++) {
                coordinatesFree.put(SeaMapCoordinatesUtils.toStringCoordinates(x, y), null);
            }
        }
        return coordinatesFree;
    }
}
