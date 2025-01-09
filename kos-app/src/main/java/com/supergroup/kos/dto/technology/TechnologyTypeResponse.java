package com.supergroup.kos.dto.technology;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class TechnologyTypeResponse {
    private Long               totalItem;
    private Long               numResearchable;
    private String             name;
    private Long               numResearchedItem;
    private Boolean            isLock;
    private TechnologyResponse techRequired;
}