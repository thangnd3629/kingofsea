package com.supergroup.admin.domain.command;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AdminUpdateCommunityBuildingCommand {

    private Long maxListingRelic;
    private Long kosProfileId;
}
