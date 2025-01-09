package com.supergroup.kos.building.domain.service.seamap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.kos.building.domain.async.OccupyCombatAsyncTask;
import com.supergroup.kos.building.domain.command.GetElementByCoordinatesCommand;
import com.supergroup.kos.building.domain.command.GetElementsByAreaCommand;
import com.supergroup.kos.building.domain.command.SaveOrUpdateElementCommand;
import com.supergroup.kos.building.domain.constant.IslandStatus;
import com.supergroup.kos.building.domain.constant.seamap.BossSeaStatus;
import com.supergroup.kos.building.domain.constant.seamap.MoveSessionType;
import com.supergroup.kos.building.domain.dto.seamap.SaveUpdateCacheElementEvent;
import com.supergroup.kos.building.domain.mapper.SeaElementMapper;
import com.supergroup.kos.building.domain.model.building.CastleBuilding;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.BossSea;
import com.supergroup.kos.building.domain.model.seamap.Invader;
import com.supergroup.kos.building.domain.model.seamap.Parcel;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.ShipElement;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.repository.persistence.building.CastleBuildingRepository;

import lombok.RequiredArgsConstructor;

/**
 * @author idev
 */
@Service
@RequiredArgsConstructor
public class MapService {

    private final SeaElementService         seaElementService;
    private final SeaElementMapper          seaElementMapper;
    private final ApplicationEventPublisher publisher;
    private final CastleBuildingRepository  castleBuildingRepository;
    private final OccupyCombatAsyncTask     occupyCombatAsyncTask;

    /**
     * Get all element in request area
     */
    public List<SeaElement> getElementsByArea(GetElementsByAreaCommand command) {
        // calculate parcel
        var parcels = seaElementService.calculateParcel(command.getCoordinates().getX(),
                                                        command.getCoordinates().getY(),
                                                        command.getWidth(),
                                                        command.getHeight());
        var elements = new ArrayList<SeaElement>();
        for (Parcel parcel : parcels) {
            var cachedElements = seaElementService.findByParcelXAndParcelYAndActiveFromCache(parcel.getX(), parcel.getY(), true);

            // map to domain model
            var elementsInParcel = seaElementMapper.toModel(cachedElements);

            // filter scout ship is going to my base
            elementsInParcel = elementsInParcel.stream()
                                               .filter(e -> {
                                                   // ignore scout move session which not user move session
                                                   if (e instanceof ShipElement) {
                                                       var ship = (ShipElement) e;
                                                       return !ship.getShipStatus().equals(MoveSessionType.SCOUT)
                                                              || ship.getKosProfile().getId().equals(command.getKosProfile().getId());
                                                   }
                                                   return true;
                                               })
                                               .collect(Collectors.toList());

            elements.addAll(elementsInParcel);
        }
        return elements;
    }

    /**
     * Get element by coordinates (x, y)
     */
    public List<SeaElement> getElementByCoordinates(GetElementByCoordinatesCommand command) {
        var cache = seaElementService.findByXAndYFromCache(command.getCoordinates().getX(), command.getCoordinates().getY());
        return seaElementMapper.toModel(cache);
    }

    /**
     * Update state element in sea map
     */
    @Transactional
    public SeaElement saveOrUpdateElement(SaveOrUpdateElementCommand command) {
        var savedElement = seaElementService.saveToDatabase(command.getElement());
        publisher.publishEvent(new SaveUpdateCacheElementEvent(command.getElement()));
        return savedElement;
    }

    @Transactional
    public void deleteElement(SeaElement seaElement) {
        seaElementService.deleteById(seaElement.getId());
    }

    @Transactional
    public void reviveBoss(BossSea bossSea) {
        bossSea.setStatus(BossSeaStatus.NORMAL);
        bossSea.setTimeRevivingEnd(null);
        bossSea.setHpLost(0L);
        saveOrUpdateElement(new SaveOrUpdateElementCommand(bossSea));
    }

    /**
     * Change status base from peace to occupied
     *
     * @param seaElement: user base want to change
     * @param invader: Who occupy this base
     */
    @Transactional
    public void changeStatusElementToOccupied(SeaElement seaElement, KosProfile invader) {
        seaElement.setInvader(new Invader().setKosProfileInvader(invader).setOccupyAt(LocalDateTime.now()));
        if (seaElement instanceof UserBase) {
            UserBase userBase = (UserBase) seaElement;
            CastleBuilding castleBuilding = userBase.getKosProfile().getCastleBuilding();
            castleBuilding.setIslandStatus(IslandStatus.WAR);
            occupyCombatAsyncTask.sendBaseOccupiedWarningNotification(userBase.getKosProfile().getUser().getId());
            castleBuildingRepository.save(castleBuilding);
        }
        saveOrUpdateElement(new SaveOrUpdateElementCommand(seaElement));
    }

    /**
     * Change status occupied base from war to peace
     *
     * @param seaElement: user base want to change
     */
    @Transactional
    public void changeElementStatusToPeace(SeaElement seaElement) {
        seaElement.setInvader(null);
        if (seaElement instanceof UserBase) {
            UserBase userBase = (UserBase) seaElement;
            CastleBuilding castleBuilding = userBase.getKosProfile().getCastleBuilding();
            castleBuilding.setIslandStatus(IslandStatus.PEACE);
            castleBuildingRepository.save(castleBuilding);
            userBase.setInitLiberateAt(null);
            if (Objects.isNull(seaElement.getBattle())) {
                occupyCombatAsyncTask.sendPeaceNotification(userBase.getKosProfile().getUser().getId());
            }
        }
        saveOrUpdateElement(new SaveOrUpdateElementCommand(seaElement));
    }
}
