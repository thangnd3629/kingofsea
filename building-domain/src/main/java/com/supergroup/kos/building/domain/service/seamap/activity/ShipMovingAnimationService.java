package com.supergroup.kos.building.domain.service.seamap.activity;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.kos.building.domain.command.SaveOrUpdateElementCommand;
import com.supergroup.kos.building.domain.dto.seamap.MoveSessionDTO;
import com.supergroup.kos.building.domain.mapper.MoveSessionMapper;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.ShipElement;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;
import com.supergroup.kos.building.domain.service.seamap.ElementsConfigService;
import com.supergroup.kos.building.domain.service.seamap.MapService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShipMovingAnimationService {
    private final MapService            mapService;
    private final ElementsConfigService elementsConfigService;
    private final MoveSessionMapper moveSessionMapper;

    @Transactional
    public ShipElement updateShipMoveAnimation(SeaActivity activity, MoveSession moveSession) {
        ShipElement shipElement = activity.getShipElement();
        if (Objects.isNull(shipElement)) {
            shipElement = new ShipElement();
            shipElement.setActive(true);
            var config = elementsConfigService.findShipElementConfig();
            shipElement.setSeaElementConfig(config);
        }
        MoveSessionDTO moveSessionDTO = moveSessionMapper.toDTO(moveSession);
        shipElement.setStart(moveSession.getStart())
                   .setEnd(moveSession.getEnd())
                   .setSpeed(moveSession.getSpeed())
                   .setKosProfile(activity.getKosProfile())
                   .setStartTime(moveSession.getTimeStart())
                   .setShipStatus(moveSessionDTO.getType())
                   .setActive(true)
                   .setCoordinate(moveSession.getStart());
        activity.setShipElement(shipElement);
        mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(shipElement));
        return shipElement;
    }
}
