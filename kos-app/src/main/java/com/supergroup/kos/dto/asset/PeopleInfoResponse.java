package com.supergroup.kos.dto.asset;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PeopleInfoResponse {
    private Long total;
    private Long idle;
}