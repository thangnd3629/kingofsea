package com.supergroup.kos.building.domain.model.scout;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ShipScoutingResult {
    private Long   id;
    private Long   level;
    private Double percentStatLevel;
    private Double percentStatQuality;
    private Long   atk1;
    private Long   atk2;
    private Long   def1;
    private Long   def2;
    private String name;
    private Long   hp;
    private Long   dodge;
    private String thumbnail;
}
