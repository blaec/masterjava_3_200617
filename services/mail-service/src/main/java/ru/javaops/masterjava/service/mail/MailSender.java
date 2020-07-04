package ru.javaops.masterjava.service.mail;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import ru.javaops.masterjava.config.Configs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class MailSender {
    static void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body) {
        String emailLog = String.format("'%s' cc '%s' subject '%s'%s",
                to, cc, subject, (log.isDebugEnabled() ? String.format("\nbody=%s", body) : ""));
        try {
            getSimpleEmail(to, cc, subject, body).send();
            log.info(String.format("E-mail sent to %s", emailLog));
        } catch (EmailException e) {
            log.warn(String.format("Failed to send mail to %s", emailLog));
        }
    }

    private static Email getSimpleEmail(List<Addressee> to, List<Addressee> cc, String subject, String body) throws EmailException {
        Config config = Configs.getConfig("mail.conf", "email");
        Email email = new SimpleEmail();
        email.setHostName(config.getString("host"));
        email.setSmtpPort(config.getInt("port"));
        email.setAuthenticator(new DefaultAuthenticator(config.getString("username"), config.getString("password")));
        email.setSSLOnConnect(config.getBoolean("useSSL"));
        email.setFrom(String.format("%s <%s>", config.getString("fromName"), config.getString("username")));
        email.setSubject(String.format("%s %s", subject, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)));
        email.setMsg(body);
        email.addTo(listToEmailsArray(to));
        email.addCc(listToEmailsArray(cc));
        return email;
    }

    private static String[] listToEmailsArray(List<Addressee> list) {
        return list.stream()
                .map(Addressee::toString)
                .collect(Collectors.toList())
                .toArray(new String[list.size()]);
    }
}
