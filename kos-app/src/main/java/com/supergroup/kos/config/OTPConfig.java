package com.supergroup.kos.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.RequiredArgsConstructor;

//@Configuration
@RequiredArgsConstructor
public class OTPConfig {
    private final Logger logger = LogManager.getLogger(OTPConfig.class);

    /**
     * Config at application-core-local.yml
     */
    @ConfigurationProperties(prefix = "bitplay.otp.twilio")
    @Component
    @Data
    class TwilioProperties {
        private String masterPhone;
        private String sid;
        private String token;
        private String serviceSid;
    }
}
