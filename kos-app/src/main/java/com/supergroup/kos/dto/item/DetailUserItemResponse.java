package com.supergroup.kos.dto.item;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.supergroup.kos.building.domain.constant.item.ItemType;
import com.supergroup.kos.building.domain.constant.item.NameSpaceKey;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DetailUserItemResponse {
    private String        id;
    private String        name;
    private NameSpaceKey  namespace;
    private ItemType      type;
    private String        thumbnail;
    private String        description;
    private Long          expiry;
    private Long          amount;
    private Boolean       isUsed;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime useTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime expiredDate;
    private Boolean       isExpired;
}
