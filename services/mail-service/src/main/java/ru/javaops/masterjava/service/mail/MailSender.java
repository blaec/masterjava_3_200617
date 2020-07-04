package ru.javaops.masterjava.service.mail;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import java.util.List;

@Slf4j
public class MailSender {
    static void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body) {
        try {
            Email email = new SimpleEmail();
            email.setHostName("smtp.yandex.ru");
            email.setSmtpPort(465);
            email.setAuthenticator(new DefaultAuthenticator("", ""));
            email.setSSLOnConnect(true);
            email.setFrom("");
            email.setSubject(subject);
            email.setMsg(body);
            email.addTo(to.get(0).getEmail());
            email.send();
            log.info("Send mail to \'" + to + "\' cc \'" + cc + "\' subject \'" + subject + (log.isDebugEnabled() ? "\nbody=" + body : ""));
        } catch (EmailException e) {
            log.error("Failed to send mail to \'" + to + "\' cc \'" + cc + "\' subject \'" + subject + (log.isDebugEnabled() ? "\nbody=" + body : ""));
        }
    }
}
