package com.supergroup.kos.dto.relic;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListingRelicRequest {
    @NotNull(message = "Listing type is not null")
    Boolean isListing;
}
