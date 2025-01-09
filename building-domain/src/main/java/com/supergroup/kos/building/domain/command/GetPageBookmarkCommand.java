package com.supergroup.kos.building.domain.command;

import org.springframework.data.domain.Pageable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class GetPageBookmarkCommand {
    private Long kosProfileId;
    private Pageable pageable;


}
