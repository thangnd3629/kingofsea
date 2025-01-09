package com.supergroup.kos.dto.profile;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class KosLocation {
    private Long x;
    private Long y;
}
