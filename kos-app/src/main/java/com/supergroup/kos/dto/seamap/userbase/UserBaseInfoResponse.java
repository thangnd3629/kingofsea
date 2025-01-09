package com.supergroup.kos.dto.seamap.userbase;

import com.supergroup.kos.dto.profile.UserInfoResponse;
import com.supergroup.kos.dto.seamap.alliance.AllianceInfoResponse;
import com.supergroup.kos.dto.seamap.elements.ElementResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserBaseInfoResponse {
    private UserInfoResponse     userInfo;
    private String               name;
    private Double               power;
    private Long                 win;
    private Long                 lose;
    private Long                 bossKilled;
    private Long                 level;
    private Long                 warshipDestroyed;
    private Long                 warshipLost;
    private ElementResponse      element;
    private AllianceInfoResponse alliance;
}
