package ru.javaops.masterjava.service.mail;

import ru.javaops.masterjava.web.handler.SoapServerSecurityHandler;

public class SecurityHandler {
    public static class SecurityInitializer extends SoapServerSecurityHandler {
        public SecurityInitializer() {
            super(MailWSClient.USER, MailWSClient.PASSWORD);
        }
    }
}
