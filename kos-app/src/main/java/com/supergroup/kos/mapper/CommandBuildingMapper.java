package com.supergroup.kos.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.supergroup.kos.building.domain.model.building.CommandBuilding;
import com.supergroup.kos.building.domain.model.ship.MotherShip;
import com.supergroup.kos.dto.building.CommandBuildingResponse;

@Mapper
public interface CommandBuildingMapper {

    //    motherShips
    @Mapping(target = "numberOfMotherShip", source = "commandBuilding.motherShips", qualifiedByName = "getNumberOfMotherShip")
    CommandBuildingResponse toDTO(CommandBuilding commandBuilding);

    List<CommandBuildingResponse> toDTOs(List<CommandBuilding> commandBuildings);

    @Named("getNumberOfMotherShip")
    default int getNumberOfMotherShip(Collection<MotherShip> motherShips) {
        return motherShips.size();
    }
}
