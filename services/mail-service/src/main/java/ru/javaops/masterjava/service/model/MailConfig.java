package ru.javaops.masterjava.service.model;

import com.typesafe.config.Config;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import ru.javaops.masterjava.config.Configs;

import javax.mail.Authenticator;
import java.nio.charset.StandardCharsets;

public class MailConfig {
    private static final MailConfig CONFIG_INSTANCE = new MailConfig(Configs.getConfig("mail.conf", "email"));

    private final String host;
    private final int port;
    private final boolean useSSL;
    private final boolean useTLS;
    private final boolean debug;
    private final String username;
    private final Authenticator auth;
    private final String fromName;

    private MailConfig(Config conf) {
        host = conf.getString("host");
        port = conf.getInt("port");
        useSSL = conf.getBoolean("useSSL");
        useTLS = conf.getBoolean("useTLS");
        debug = conf.getBoolean("debug");
        username = conf.getString("username");
        auth = new DefaultAuthenticator(username, conf.getString("password"));
        fromName = conf.getString("fromName");
    }

    private <T extends Email> T prepareEmail(T email) throws EmailException {
        email.setHostName(host);
        if (useSSL) {
            email.setSslSmtpPort(String.valueOf(port));
        } else {
            email.setSmtpPort(port);
        }
        email.setSSLOnConnect(useSSL);
        email.setStartTLSEnabled(useTLS);
        email.setDebug(debug);
        email.setAuthenticator(auth);
        email.setCharset(StandardCharsets.UTF_8.name());
        email.setFrom(username, fromName);
        return email;
    }

    public static HtmlEmail createHtmlEmail() throws EmailException {
        return CONFIG_INSTANCE.prepareEmail(new HtmlEmail());
    }
}
