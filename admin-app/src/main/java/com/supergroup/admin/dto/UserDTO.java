package com.supergroup.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String email;
    private String     tag;
    private String userStatus;
}
