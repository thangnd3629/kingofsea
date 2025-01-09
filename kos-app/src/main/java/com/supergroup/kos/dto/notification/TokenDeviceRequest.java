package com.supergroup.kos.dto.notification;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDeviceRequest {
    @NotNull
    @NotEmpty
    String tokenDevice;
}
