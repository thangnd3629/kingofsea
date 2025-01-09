package com.supergroup.kos.building.domain.service.seamap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.constant.seamap.BossSeaStatus;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.dto.seamap.DeleteCacheElementEvent;
import com.supergroup.kos.building.domain.dto.seamap.SeaElementCache;
import com.supergroup.kos.building.domain.mapper.SeaElementMapper;
import com.supergroup.kos.building.domain.model.seamap.BossSea;
import com.supergroup.kos.building.domain.model.seamap.ElementRefresh;
import com.supergroup.kos.building.domain.model.seamap.Invader;
import com.supergroup.kos.building.domain.model.seamap.Parcel;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.ShipElement;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.repository.cache.seamap.SeaElementCacheRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.BossSeaElementRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.UserBaseRepository;
import com.supergroup.kos.building.domain.service.profile.UserLevelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class SeaElementService {
    private final SeaElementRepository<SeaElement> elementPersistenceRepository;
    private final BossSeaElementRepository         bossSeaSeaElementRepository;
    private final SeaElementCacheRepository        elementCacheRepository;
    private final UserBaseRepository               userBaseRepository;
    private final SeaElementMapper                 seaElementMapper;
    private final UserLevelService                 userLevelService;
    private final ApplicationEventPublisher        publisher;

    @Value("${seamap.parcel-size}")
    public Integer PARCEL_SIZE;

    @PostConstruct
    private void postConstruct() {
        // check parcel size
        if (Objects.isNull(PARCEL_SIZE)) {
            throw new RuntimeException("Parcel size must not be null");
        }
    }

    public List<BossSea> getAllRevivingBossSea() {
        return bossSeaSeaElementRepository.findByStatuses(List.of(BossSeaStatus.REVIVING));
    }

    public SeaElement saveToDatabase(SeaElement seaElement) {
        validateElement(seaElement);
        return elementPersistenceRepository.save(seaElement);
    }

    public SeaElementCache saveToCache(SeaElement seaElement) {
        validateElement(seaElement);
        var cacheModel = seaElementMapper.map(seaElement);
        return elementCacheRepository.save(cacheModel);
    }

    public List<SeaElement> getAllFromDatabase() {
        return elementPersistenceRepository.findAll();
    }

    public void deleteAllFromDatabase() {
        elementPersistenceRepository.deleteAll();
    }

    @Transactional
    public void validateElement(SeaElement seaElement) {
        // validate data
        if (Objects.isNull(seaElement.getCoordinates()) || Objects.isNull(seaElement.getCoordinates().getX()) || Objects.isNull(
                seaElement.getCoordinates().getY())) {
            throw new IllegalArgumentException("coordinate must not be null");
        }

        // validate x and y
        seaElement.setX(seaElement.getCoordinates().getX());
        seaElement.setY(seaElement.getCoordinates().getY());

        // validate parcel
        var parcel = calculateParcel(seaElement.getX(), seaElement.getY());
        seaElement.setParcelX(parcel.getX());
        seaElement.setParcelY(parcel.getY());

        // validate use base level
        if (seaElement instanceof UserBase) {
            var userBase = (UserBase) seaElement;
            if (Objects.nonNull(userBase.getKosProfile())) {
                var kosProfileId = userBase.getKosProfile().getId();
                userBase.getKosProfile().setLevel(userLevelService.getLevel(kosProfileId));
            } else {
                userBase.setKosProfile(null);
            }
        }

        validateParcelElement(seaElement);
    }

    private void validateParcelElement(SeaElement seaElement) {
        var parcel = calculateParcel(seaElement.getX(), seaElement.getY());
        seaElement.setParcelX(parcel.getX()).setParcelY(parcel.getY());
    }

    public List<Parcel> calculateParcel(Long x, Long y, Long width, Long height) {
        var vectorX = width / 2;
        var vectorY = height / 2;
        var rawParcel = List.of(calculateParcel(x - vectorX, y - vectorY),
                                calculateParcel(x - vectorX, y + vectorY),
                                calculateParcel(x + vectorX, y - vectorY),
                                calculateParcel(x + vectorX, y + vectorY));
        var minX = rawParcel.stream().min(Comparator.comparingInt(Parcel::getX)).orElseThrow().getX();
        var maxX = rawParcel.stream().max(Comparator.comparingInt(Parcel::getX)).orElseThrow().getX();
        var minY = rawParcel.stream().min(Comparator.comparingInt(Parcel::getY)).orElseThrow().getY();
        var maxY = rawParcel.stream().max(Comparator.comparingInt(Parcel::getY)).orElseThrow().getY();
        var res = new ArrayList<Parcel>();
        for (int i = minX; i <= maxX; i++) {
            for (int j = minY; j <= maxY; j++) {
                res.add(new Parcel(i, j));
            }
        }
        return res;
    }

    private Parcel calculateParcel(Long x, Long y) {
        var parcelX = Double.valueOf(Math.ceil(Math.abs(x) / (PARCEL_SIZE * 1.0)) * x / Math.abs(x));
        var parcelY = Double.valueOf(Math.ceil(Math.abs(y) / (PARCEL_SIZE * 1.0)) * y / Math.abs(y));
        return new Parcel(parcelX.intValue(), parcelY.intValue());
    }

    /**
     * Find user base from persistence database
     */
    @Transactional
    public UserBase findUserBaseByKosProfileIdFromDatabase(Long kosProfileId) {
        return userBaseRepository.findUserBaseByKosProfileId(kosProfileId)
                                 .orElseThrow(() -> KOSException.of(ErrorCode.USER_BASE_NOT_FOUND));
    }

    /**
     * Get all element by type persistence database
     */
    @Transactional
    public List<SeaElement> findSeaElementByTypeFromCache(List<SeaElementType> types) {
        var result = new ArrayList<SeaElement>();
        for (SeaElementType type : types) {
            var list = elementCacheRepository.findBySeaElementConfig_Type(type);
            result.addAll(seaElementMapper.toModel(list));
        }
        return result;
    }

    @Transactional
    public List<SeaElement> findSeaElementByTypeFromDatabase(List<SeaElementType> types) {
        return elementPersistenceRepository.findByListType(types.stream().map(Enum::name).collect(Collectors.toList()));
    }

    /**
     * Delete from cache and persistence database
     */
    @Transactional
    public void deleteSeaElementByTypeFromCache(List<SeaElementType> types) {
        // delete from cache
        for (SeaElementType type : types) {
            elementCacheRepository.deleteBySeaElementConfigType(type);
        }
    }

    public List<SeaElementCache> findByParcelXAndParcelYAndActiveFromCache(Integer x, Integer y, boolean active) {
        return elementCacheRepository.findByParcelXAndParcelYAndActive(x, y, active);
    }

    public SeaElement getElementById(Long id) {
        return elementPersistenceRepository.findById(id).orElseThrow(() -> new KOSException(ErrorCode.SEA_ELEMENT_NOT_FOUND));
    }

    public SeaElementCache getElementByIdFromCache(Long id) {
        return elementCacheRepository.findById(id).orElseThrow(() -> new KOSException(ErrorCode.SEA_ELEMENT_NOT_FOUND));
    }

    public SeaElement findElementById(Long id) {
        // return null if not found ,don't throw ex
        Optional<SeaElement> nullable = elementPersistenceRepository.findById(id);
        if (nullable.isPresent()) {
            return nullable.get();
        } else {
            return null;
        }
    }

    public List<SeaElementCache> findByXAndYFromCache(Long x, Long y) {
        return elementCacheRepository.findByXAndY(x, y);

    }

    public SeaElement findByXAndYFromDatabase(Long x, Long y) {
        List<SeaElement> seaElements =  elementPersistenceRepository.findByXAndY(x, y);
        seaElements = seaElements.stream().filter(s-> !(s instanceof ShipElement)).collect(Collectors.toList());
        var size = seaElements.size();
        if(size == 0) {
            return null;
        } else if (size == 1) {
            return seaElements.get(0);
        } else {
            log.info("WARRING many element in x = {}, y = {}", x, y);
            throw KOSException.of(ErrorCode.MANY_ELEMENT_IN_COORDINATES);

        }

    }

    public List<SeaElement> saveAllToDatabase(List<SeaElement> seaElementListNew) {
        return elementPersistenceRepository.saveAll(seaElementListNew);
    }

    public void deleteAllFromDatabase(List<SeaElement> listElement) {
        elementPersistenceRepository.deleteAll(listElement);
    }

    public List<ElementRefresh> getElementsRefresh() {
        return elementPersistenceRepository.getElementsRefresh();
    }

    @Transactional
    public Integer updateDeletedElement(List<Long> ids, Boolean deleted) {
        return elementPersistenceRepository.updateDeletedElement(ids, deleted);
    }

    public void deleteByIdCache(Long id) {
        elementCacheRepository.deleteById(id);
    }

    public void deleteById(Long id) {
        publisher.publishEvent(new DeleteCacheElementEvent(id));
        elementPersistenceRepository.deleteById(id);
    }

    public List<SeaElement> getElementsActive() {
        return elementPersistenceRepository.getElementsActive();
    }

    public Invader invader(Long elementId) {
        var element = elementPersistenceRepository.findById(elementId)
                                                  .orElseThrow(() -> KOSException.of(ErrorCode.ELEMENT_NOT_FOUND));
        if (!element.isOccupied()) {
            throw KOSException.of(ErrorCode.ELEMENT_IS_UNOCCUPIED);
        }
        return element.getInvader();
    }

}
