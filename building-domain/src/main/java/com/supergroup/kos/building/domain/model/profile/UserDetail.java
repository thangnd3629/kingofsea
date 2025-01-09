package com.supergroup.kos.building.domain.model.profile;

import java.time.LocalDateTime;

public class UserDetail {
    String        id;
    Long          amount;
    String        namespace;
    String        type;
    String        name;
    String        thumbnail;
    String        description;
    Long          expiry;
    String        unit;
    Boolean       isUsed;
    LocalDateTime useTime;
    LocalDateTime expiredDate;
    LocalDateTime isExpired;
}
