package com.supergroup.kos.dto.profile;

import com.supergroup.kos.building.domain.model.scout.Location;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserInfoResponse {
    private Long     id;
    private Long     kosProfileId;
    private String   email;
    private String   username;
    private String   avatarUrl;
    private Location location;
}
