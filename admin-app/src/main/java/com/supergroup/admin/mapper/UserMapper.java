package com.supergroup.admin.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.supergroup.admin.dto.UserDTO;
import com.supergroup.auth.domain.model.User;

@Mapper
public interface UserMapper {
    UserDTO toDTO(User user);
    List<UserDTO> toDTOs(List<User> userList);
}
