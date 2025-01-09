package com.supergroup.kos.api.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.auth.domain.service.ResendOtpService;
import com.supergroup.kos.constant.HeaderKey;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/verify")
@RequiredArgsConstructor
public class VerifySessionRestController {

    private final ResendOtpService resendOtpService;

    @PostMapping("/resend")
    public ResponseEntity<?> resendOtp(@RequestHeader(HeaderKey.VERIFY_TOKEN) String verifyToken) {
        resendOtpService.resendVerifyCode(verifyToken);
        return ResponseEntity.ok().build();
    }
}
