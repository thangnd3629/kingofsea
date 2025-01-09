package com.supergroup.email.service;

import java.io.IOException;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

public class SendgridEmailSender implements EmailSender {

    private Email         masterEmail;
    private SendGrid      sendGrid;

    public SendgridEmailSender(String masterEmail, String apiKey) {
        this.masterEmail = new Email(masterEmail);
        sendGrid = new SendGrid(apiKey);
    }

    @Override
    public boolean send(String subject, String content, String to, EmailType type) {
        try {
            var toEmail = new Email(to);
            var mailContent = new Content(type.getType(), content);
            var mail = new Mail(masterEmail, subject, toEmail, mailContent);

            var request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            var res = sendGrid.api(request);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
