package com.supergroup.kos.mapper.seamap.activity;

import java.util.List;
import java.util.Objects;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.supergroup.kos.building.domain.mapper.MoveSessionMapper;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.model.seamap.movesession.MissionType;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;
import com.supergroup.kos.building.domain.service.seamap.activity.LineUpService;
import com.supergroup.kos.dto.seamap.activity.EscortShipSquadDTO;
import com.supergroup.kos.dto.seamap.activity.SeaActivityDTO;
import com.supergroup.kos.dto.seamap.activity.ShipLineUpDTO;

@Mapper(uses = { MoveSessionMapper.class })
public interface SeaActivityMapper {

    @Mapping(target = "shipLineUp", ignore = true)
    SeaActivityDTO toDto(SeaActivity activity,
                         @Context LineUpMapper lineUpMapper,
                         @Context SeaElementService seaElementService,
                         @Context LineUpService lineUpService);

    @Mapping(target = "shipLineUp", ignore = true)
    List<SeaActivityDTO> toDtos(List<SeaActivity> activityList,
                                @Context LineUpMapper lineUpMapper,
                                @Context SeaElementService seaElementService,
                                @Context LineUpService lineUpService);

    @AfterMapping
    default void toShipLineUpDTO(@MappingTarget SeaActivityDTO activityDTO,
                                 SeaActivity activity,
                                 @Context LineUpMapper lineUpMapper,
                                 @Context SeaElementService seaElementService,
                                 @Context LineUpService lineUpService) {
        ShipLineUpDTO shipLineUpDTO = new ShipLineUpDTO();
        ShipLineUp lineUp = activity.getLineUp();
        if (Objects.nonNull(lineUp)) {
            shipLineUpDTO = lineUpMapper.toDto(lineUp);
            shipLineUpDTO.setShipUnits(lineUpService.getCurrentShipUnitsCount(lineUp));
            shipLineUpDTO.getMotherShip().setCurrentLocation(activity.getCurrentLocation());

            // getPower
            Double totalPower = lineUpService.getPowerLineup(lineUp);
            shipLineUpDTO.getMotherShip().setPower(totalPower);
        }
        if (Objects.nonNull(activity.getScout())) {
            if (activity.getActiveMoveSession().getMissionType().equals(MissionType.RETURN)) {
                shipLineUpDTO.setNumberArmy(activity.getScout().getSoliderRemain());
            } else {
                shipLineUpDTO.setNumberArmy(activity.getScout().getNumberArmy());
            }

        }

        activityDTO.setShipLineUp(shipLineUpDTO);
        if (Objects.nonNull(activity.getActiveMoveSession())) {
            if (Objects.nonNull(activity.getCurrentLocation())) {

                SeaElement currentLocation = seaElementService.findElementById(activity.getActiveMoveSession().getDestinationElementId());
                if (Objects.nonNull(currentLocation) && Objects.nonNull(currentLocation.getBattle())) {
                    var battle = currentLocation.getBattle();
                    var currentRound = 1L;
                    if (Objects.nonNull(battle.getCurrentRound()) && battle.getCurrentRound() > 1) {
                        currentRound = battle.getCurrentRound();
                    }
                    activityDTO.setCurrentRound(currentRound);
                }
            }
        }
    }

}
