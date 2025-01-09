package com.supergroup.kos.upgrading.consumer;

import java.util.Calendar;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class TimezoneConfig {

    @PostConstruct
    public void initialize() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        log.info("Setting current TZ=" + Calendar.getInstance().getTimeZone());
    }
}
