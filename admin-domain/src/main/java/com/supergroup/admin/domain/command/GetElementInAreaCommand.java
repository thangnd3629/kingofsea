package com.supergroup.admin.domain.command;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class GetElementInAreaCommand {
    private Long x;
    private Long y;
    private Long width;
    private Long height;
}
