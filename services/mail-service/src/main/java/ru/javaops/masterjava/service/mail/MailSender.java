package ru.javaops.masterjava.service.mail;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import ru.javaops.masterjava.config.Configs;
import ru.javaops.masterjava.service.model.MailConfig;
import ru.javaops.masterjava.service.model.SentMailResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class MailSender {

    static void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body) {
        String emailLog = String.format("'%s' cc '%s' subject '%s'%s",
                to, cc, subject, (log.isDebugEnabled() ? String.format("\nbody=%s", body) : ""));
        String result = "OK";
        List<String> toEmails = listToEmailsArray(to);
        List<String> ccEmails = listToEmailsArray(cc);
        try {
            val email = MailConfig.createHtmlEmail();
            email.setSubject(subject);
            email.setHtmlMsg(body);
            for (Addressee addressee : to) {
                email.addTo(addressee.getEmail(), addressee.getName());
            }
            for (Addressee addressee : cc) {
                email.addCc(addressee.getEmail(), addressee.getName());
            }
            email.send();
        } catch (EmailException e) {
            log.error(e.getMessage(), e);
            result = e.getMessage();
        }
        toEmails.addAll(ccEmails);
        SentMailResult mailResult = new SentMailResult(LocalDateTime.now(), result, toEmails.stream().map(String::toString).collect(Collectors.joining("; ")));
        mailResult.save();
    }

    private static List<String> listToEmailsArray(List<Addressee> list) {
        Pattern pattern = Pattern.compile("^(.+)@(.+)$");
        return list.stream()
                .filter(a -> pattern.matcher(a.getEmail()).matches())
                .map(Addressee::toString)
                .collect(Collectors.toList());
    }

    private static String[] listToArray(List<String> input) {
        String[] output = new String[input.size()];
        input.toArray(output);
        return output;
    }
}
