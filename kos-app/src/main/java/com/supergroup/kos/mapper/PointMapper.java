package com.supergroup.kos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.supergroup.kos.building.domain.model.point.Point;
import com.supergroup.kos.dto.point.PointResponse;

@Mapper
public interface PointMapper {
    @Mapping(target = "mp", source = "point.mpPoint")
    @Mapping(target = "gp", source = "point.gpPoint")
    @Mapping(target = "tp", source = "point.tpPoint")
    PointResponse toDTO(Point point);
}
