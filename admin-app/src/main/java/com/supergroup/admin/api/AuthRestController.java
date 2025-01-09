package com.supergroup.admin.api;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.admin.domain.command.AdminLoginCommand;
import com.supergroup.admin.domain.service.AdminAccountService;
import com.supergroup.admin.dto.AdminLoginRequest;
import com.supergroup.admin.dto.AuthLoginResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AdminAccountService adminAccountService;

    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        return ResponseEntity.ok(new AuthLoginResponse().setAccessToken(adminAccountService.login(new AdminLoginCommand(request.getUsername(),
                                                                                                                        request.getPassword()))
                                                                                           .getToken()));
    }

//    @GetMapping("/logout")
//    public ResponseEntity<?> logout(@AuthenticationPrincipal AdminAccount account) {
//        authAdminService.logout(account);
//        return ResponseEntity.ok().build();
//    }
}
