package com.supergroup.kos.mapper.seamap;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.model.config.seamap.BossSeaConfig;
import com.supergroup.kos.building.domain.model.config.seamap.ResourceIslandConfig;
import com.supergroup.kos.building.domain.model.config.seamap.SeaElementConfig;
import com.supergroup.kos.building.domain.model.config.seamap.ShipElementConfig;
import com.supergroup.kos.building.domain.model.config.seamap.UserBaseConfig;
import com.supergroup.kos.dto.data.ElementsConfigResponse;

@Mapper()
public interface ElementsConfigMapper {
    ElementsConfigResponse toUserIslandConfigResponse(UserBaseConfig config);

    ElementsConfigResponse toBossConfigResponse(BossSeaConfig config);

    ElementsConfigResponse toResourceResponse(ResourceIslandConfig config);

    ElementsConfigResponse toShipElementConfig(ShipElementConfig config);

    default ElementsConfigResponse map(SeaElementConfig config) {
        ElementsConfigResponse response = new ElementsConfigResponse();
        if (config instanceof UserBaseConfig) {
            response = toUserIslandConfigResponse((UserBaseConfig) config);
        } else if (config instanceof ResourceIslandConfig) {
            response = toResourceResponse((ResourceIslandConfig) config);
        } else if (config instanceof BossSeaConfig) {
            response = toBossConfigResponse((BossSeaConfig) config);
        } else if (config instanceof ShipElementConfig) {
            response = toShipElementConfig((ShipElementConfig) config);
        } else {
            throw KOSException.of(ErrorCode.TYPE_ELEMENT_NOT_FOUND);
        }
        return response.setWidth(config.getOccupied().getWidth())
                       .setHeight(config.getOccupied().getHeight())
                       .setLength(config.getOccupied().getLength());
    }

    default List<ElementsConfigResponse> maps(List<SeaElementConfig> configLists) {
        List<ElementsConfigResponse> list = new ArrayList<>();
        for (SeaElementConfig s : configLists) {
            list.add(this.map(s));
        }
        return list;
    }
}
