package com.supergroup.kos.building.domain.dto.seamap;

import java.time.LocalDateTime;

import com.supergroup.kos.building.domain.constant.seamap.MoveSessionType;
import com.supergroup.kos.building.domain.dto.profile.KosProfileCache;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ShipElementCache extends SeaElementCache {
    private Coordinates     start;
    private Coordinates     end;
    private Double          speed;
    private KosProfileCache kosProfile;
    private LocalDateTime   startTime;
    /** legacy code, to be remove in sp7 **/
    private MoveSessionType shipStatus;
}
