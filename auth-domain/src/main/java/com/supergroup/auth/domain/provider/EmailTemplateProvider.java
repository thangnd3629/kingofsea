package com.supergroup.auth.domain.provider;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.supergroup.auth.domain.constant.VerifyReason;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailTemplateProvider {

    private static final String REGEX_FORGOT_PASSWORD  = "[link_forgot_password_page]";
    private static final String REGEX_CHANGE_EMAIL     = "[link_change_email_page]";
    private static final String REGEX_NAME_REPLACE     = "[name]";
    private static final String REGEX_OTP_REPLACE      = "[otp]";
    private static final String REGEX_FACEBOOK_REPLACE = "[facebook_channel]";
    private static final String REGEX_MEDIUM_REPLACE   = "[medium_channel]";
    private static final String REGEX_DISCORD_REPLACE  = "[discord_channel]";
    private static final String REGEX_TELEGRAM_REPLACE = "[telegram_channel]";
    private static final String REGEX_WEBSITE_URL      = "[website_url]";
    private static final String REGEX_TWITTER_REPLACE  = "[twitter_channel]";
    private static final String REGEX_YOUTUBE_REPLACE  = "[youtube_channel]";
    private static final String REGEX_REASON_REPLACE   = "[reason]";

    @Value("classpath:email/template/forgot_password.html")
    Resource forgotPasswordTemplateRes;
    @Value("classpath:email/template/change_email.html")
    Resource changeEmailTemplateRes;
    @Value("classpath:email/template/verify_code.html")
    Resource verifyTemplateRes;
    @Value("classpath:email/template/wellcome_email.html")
    Resource welcomeTemplateRes;

    public Optional<String> forgotPasswordHtml(String linkToForgotPage, String username) {
        var html = getStringFromResource(forgotPasswordTemplateRes);
        if (html.isPresent()) {
            var res = html.get().replace(REGEX_FORGOT_PASSWORD, linkToForgotPage);
            res = res.replace(REGEX_NAME_REPLACE, username);
            res = fillCommunityChannel(res);
            return Optional.of(res);
        }
        return Optional.empty();
    }

    public Optional<String> changeEmailHtml(String linkChangeEmailPage, String username) {
        var html = getStringFromResource(changeEmailTemplateRes);
        if (html.isPresent()) {
            var res = html.get().replace(REGEX_CHANGE_EMAIL, linkChangeEmailPage);
            res = res.replace(REGEX_NAME_REPLACE, username);
            res = fillCommunityChannel(res);
            return Optional.of(res);
        }
        return Optional.empty();
    }

    public Optional<String> verifyCode(String otp, String username, VerifyReason reason) {
        var html = getStringFromResource(verifyTemplateRes);
        if (html.isPresent()) {
            var res = html.get().replace(REGEX_OTP_REPLACE, otp);
            res = res.replace(REGEX_NAME_REPLACE, username);
            res = res.replace(REGEX_REASON_REPLACE, reason.getName());
            res = fillCommunityChannel(res);
            return Optional.of(res);
        }
        return Optional.empty();
    }

    public Optional<String> welcomeTemplate(String username) {
        var html = getStringFromResource(welcomeTemplateRes);
        if (html.isPresent()) {
            var res = html.get().replace(REGEX_NAME_REPLACE, username);
            res = fillCommunityChannel(res);
            return Optional.of(res);
        }
        return Optional.empty();
    }

    private Optional<String> getStringFromResource(Resource resource) {
        try {
            return Optional.of(new String(resource.getInputStream().readAllBytes()));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private String fillCommunityChannel(String html) {
        // TODO implement here
        return html;
    }

}
