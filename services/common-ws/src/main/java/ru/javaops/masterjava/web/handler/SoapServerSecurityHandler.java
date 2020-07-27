package ru.javaops.masterjava.web.handler;

import com.sun.xml.ws.api.handler.MessageHandlerContext;

public abstract class SoapServerSecurityHandler extends SoapBaseHandler {
    private String authHandler;

    public SoapServerSecurityHandler(String authHandler) {
        this.authHandler = authHandler;
    }

    @Override
    public boolean handleMessage(MessageHandlerContext context) {
        return true;
    }

    @Override
    public boolean handleFault(MessageHandlerContext context) {
        return true;
    }

    abstract protected boolean isRequest(boolean isOutbound);

    public static class ClientHandler extends SoapServerSecurityHandler {
        public ClientHandler(String auth) {
            super(auth);
        }

        @Override
        protected boolean isRequest(boolean isOutbound) {
            return isOutbound;
        }
    }
}
