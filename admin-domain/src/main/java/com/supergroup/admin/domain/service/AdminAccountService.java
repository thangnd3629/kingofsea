package com.supergroup.admin.domain.service;

import org.springframework.stereotype.Service;

import com.supergroup.admin.domain.command.AdminLoginCommand;
import com.supergroup.admin.domain.command.CreateAdminAccountCommand;
import com.supergroup.admin.domain.model.AdminAccount;
import com.supergroup.admin.domain.provider.AdminJwtTokenProvider;
import com.supergroup.admin.domain.repository.AdminAccountRepository;
import com.supergroup.admin.domain.ultil.PasswordUtil;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminAccountService {
    private final AdminAccountRepository adminAccountRepository;
    private final AdminJwtTokenProvider  adminJwtTokenProvider;

    public AdminAccount login(AdminLoginCommand command) {
        AdminAccount account = adminAccountRepository.findByUsername(command.getUsername()).orElseThrow(
                () -> KOSException.of(ErrorCode.USER_NOT_FOUND));
        if (checkPassword(command.getPassword(), account.getPassword())) {
            return account.setToken(adminJwtTokenProvider.generateAdminAccessToken(account.getId()));
        } else {
            throw KOSException.of(ErrorCode.PASSWORD_IS_WRONG);
        }
    }

    /**
     * create new admin account
     * */
    public AdminAccount create(CreateAdminAccountCommand command) {
        var passwordUtil = new PasswordUtil();
        var account = new AdminAccount().setUsername(command.getUsername())
                                        .setPassword(passwordUtil.encode(command.getPassword()));
        return adminAccountRepository.save(account);
    }

    private boolean checkPassword(String actual, String encodedPassword) {
        PasswordUtil passwordUtil = new PasswordUtil();
        return passwordUtil.check(actual, encodedPassword);
    }
}
