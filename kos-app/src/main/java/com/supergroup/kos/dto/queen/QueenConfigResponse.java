
package com.supergroup.kos.dto.queen;

import com.supergroup.core.constant.BaseStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class QueenConfigResponse {
    private Long       id;
    private String     name;
    private Long       mp;
    private String     thumbnail;
    private Boolean    isNew;
    private Boolean    isExist;
    private BaseStatus status;
}

