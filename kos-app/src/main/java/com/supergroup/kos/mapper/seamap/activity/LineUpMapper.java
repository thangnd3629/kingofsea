package com.supergroup.kos.mapper.seamap.activity;

import java.util.Collection;
import java.util.List;

import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.dto.seamap.activity.ShipLineUpDTO;

public interface LineUpMapper {
    ShipLineUpDTO toDto(ShipLineUp model);
    List<ShipLineUpDTO> toDtos(Collection<ShipLineUp> models);
}
