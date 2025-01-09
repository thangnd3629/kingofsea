package com.supergroup.admin.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.supergroup.admin.dto.PointResponse;
import com.supergroup.kos.building.domain.model.point.Point;

@Mapper
public interface PointMapper {
    @Mapping(target = "mp", source = "point.mpPoint")
    @Mapping(target = "gp", source = "point.gpPoint")
    @Mapping(target = "tp", source = "point.tpPoint")
    PointResponse toDTO(Point point);
}
