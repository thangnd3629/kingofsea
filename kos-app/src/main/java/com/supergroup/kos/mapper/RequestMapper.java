package com.supergroup.kos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.supergroup.auth.domain.command.ChangeEmailCommand;
import com.supergroup.auth.domain.command.ChangePasswordCommand;
import com.supergroup.auth.domain.command.ForgotPasswordCommand;
import com.supergroup.auth.domain.command.LoginWithUsernameAndPasswordCommand;
import com.supergroup.auth.domain.command.RegisterCommand;
import com.supergroup.auth.domain.model.User;
import com.supergroup.kos.dto.auth.ChangeEmailRequest;
import com.supergroup.kos.dto.auth.ChangePasswordRequest;
import com.supergroup.kos.dto.auth.ForgotPasswordRequest;
import com.supergroup.kos.dto.auth.LoginByUsernameAndPasswordRequest;
import com.supergroup.kos.dto.auth.RegisterRequest;

@Mapper
public interface RequestMapper {

    RegisterCommand toCommand(RegisterRequest request);

    @Mappings({
            @Mapping(source = "password", target = "rawPassword")
    })
    LoginWithUsernameAndPasswordCommand toCommand(LoginByUsernameAndPasswordRequest request);

    ForgotPasswordCommand toCommand(ForgotPasswordRequest request);

    @Mappings({
            @Mapping(target = "user", expression = "java(user)")
    })
    ChangePasswordCommand toCommand(ChangePasswordRequest request, User user);

    @Mappings({
            @Mapping(target = "user", expression = "java(user)"),
    })
    ChangeEmailCommand toCommand(ChangeEmailRequest request, User user);
}
