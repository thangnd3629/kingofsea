package com.supergroup.kos.building.domain.model.config.seamap;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ZoneSeaConfig {
    private Long                    radius;
    private List<SizeZoneSeaConfig> sizeZoneSea;

    /**
     *****************
     *               *
     *        radius *
     *       O------>*
     *               *
     *               *
     * ***************
     */


    /**
     *********************************
     *    **********************     *
     *    *                    *     *
     *    *    *************   *     *
     *    *    *           *   *     *
     *    *    *     O.-r1 * r2*  r3 *
     *    *    *           *   *     *
     *    *    *           *   *     *
     *    *    *************   *     *
     *    *                    *     *
     *    **********************     *
     * ******************************
     */
}
