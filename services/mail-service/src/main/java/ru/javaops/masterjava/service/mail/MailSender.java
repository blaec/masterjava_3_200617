package ru.javaops.masterjava.service.mail;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import ru.javaops.masterjava.config.Configs;
import ru.javaops.masterjava.service.model.SentMailResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class MailSender {

    static void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body) {
        String emailLog = String.format("'%s' cc '%s' subject '%s'%s",
                to, cc, subject, (log.isDebugEnabled() ? String.format("\nbody=%s", body) : ""));
        String result;
        List<String> toEmails = new ArrayList<>();
        List<String> ccEmails = new ArrayList<>();
        try {
            toEmails = listToEmailsArray(to);
            ccEmails = listToEmailsArray(cc);
            getSimpleEmail(toEmails, ccEmails, subject, body).send();
            log.info(String.format("E-mail sent to %s", emailLog));
            result = "success";
        } catch (EmailException e) {
            log.warn(String.format("Failed to send mail to %s", emailLog));
            result = "failure";
        }
        toEmails.addAll(ccEmails);
        SentMailResult mailResult = new SentMailResult(LocalDateTime.now(), result, toEmails.stream().map(String::toString).collect(Collectors.joining("; ")));
        mailResult.save();
    }

    private static Email getSimpleEmail(List<String> to, List<String> cc, String subject, String body) throws EmailException {
        Config config = Configs.getConfig("mail.conf", "email");
        Email email = new SimpleEmail();
        email.setHostName(config.getString("host"));
        email.setSmtpPort(config.getInt("port"));
        email.setAuthenticator(new DefaultAuthenticator(config.getString("username"), config.getString("password")));
        email.setSSLOnConnect(config.getBoolean("useSSL"));
        email.setFrom(String.format("%s <%s>", config.getString("fromName"), config.getString("username")));
        email.setSubject(String.format("%s %s", subject, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)));
        email.setMsg(body);
        String[] toEmails = listToArray(to);
        if (toEmails.length > 0) {
            email.addTo(toEmails);
        }
        String[] ccEmails = listToArray(cc);
        if (ccEmails.length > 0) {
            email.addCc(ccEmails);
        }
        return email;
    }

    private static List<String> listToEmailsArray(List<Addressee> list) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("^(.+)@(.+)$");
        for (Addressee addressee : list) {
            Matcher matcher = pattern.matcher(addressee.getEmail());
            if (matcher.matches()) {
                result.add(addressee.toString());
            }
        }
        return result;
    }

    private static String[] listToArray(List<String> input) {
        String[] output = new String[input.size()];
        input.toArray(output);
        return output;
    }
}
