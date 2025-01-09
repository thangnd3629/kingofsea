package com.supergroup.email.service;

/**
 * Email sender
 * */
public interface EmailSender {
    boolean send(String subject, String content, String to, EmailType type);
}
