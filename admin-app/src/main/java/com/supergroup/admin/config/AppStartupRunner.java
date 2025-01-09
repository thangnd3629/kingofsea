package com.supergroup.admin.config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.annotation.PreDestroy;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("!local")
public class AppStartupRunner implements ApplicationRunner {

    private final TelegramBot telegramBot;
    private final Environment environment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var messageStr = String.format("%s KOS Admin is started ", Arrays.toString(environment.getActiveProfiles()));
        log.info(messageStr);
        var version = FileUtils.readFileToString(new File("./VERSION.txt"), Charsets.UTF_8);
        var description = FileUtils.readFileToString(new File("./CHANGELOG.txt"), Charsets.UTF_8);
        var message = new SendMessage("-628648424", messageStr + "\nVersion: " + version + "\nDescription:\n" + description);
        telegramBot.execute(message);
    }

    @PreDestroy
    public void onDestroy() throws IOException {
        var messageStr = String.format("%s KOS Admin is stop ", Arrays.toString(environment.getActiveProfiles()));
        log.info(messageStr);
        var version = FileUtils.readFileToString(new File("./VERSION.txt"), Charsets.UTF_8);
        var message = new SendMessage("-628648424", messageStr + "\nVersion: " + version);
        telegramBot.execute(message);
    }
}