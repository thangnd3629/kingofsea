package com.supergroup.kos.dto.data;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DataStaticResponse {
    private List<ElementsConfigResponse> elements;
}
