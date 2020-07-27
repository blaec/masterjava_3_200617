package ru.javaops.masterjava.web.handler;

import com.sun.xml.ws.api.handler.MessageHandlerContext;
import ru.javaops.masterjava.web.Statistics;

public abstract class SoapStatisticsHandlers extends SoapBaseHandler {
    private final Long startTime;

    public SoapStatisticsHandlers(Long startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean handleMessage(MessageHandlerContext context) {
        String payload = String.format("%s %s",
                isRequest(isOutbound(context)) ? "SOAP request:" : "SOAP response:",
                context.getMessage().getPayloadLocalPart());
        Statistics.count(payload, startTime, Statistics.RESULT.SUCCESS);
        return true;
    }

    @Override
    public boolean handleFault(MessageHandlerContext context) {
        String payload = String.format("%s %s",
                isRequest(isOutbound(context)) ? "SOAP request:" : "SOAP response:",
                context.getMessage().getPayloadLocalPart());
        Statistics.count(payload, startTime, Statistics.RESULT.FAIL);
        return true;
    }

    abstract protected boolean isRequest(boolean isOutbound);

    public static class ClientHandler extends SoapStatisticsHandlers {
        public ClientHandler(Long startTime) {
            super(startTime);
        }

        @Override
        protected boolean isRequest(boolean isOutbound) {
            return isOutbound;
        }
    }

    public static class ServerHandler extends SoapStatisticsHandlers {

        public ServerHandler() {
            super(System.currentTimeMillis());
        }

        @Override
        protected boolean isRequest(boolean isOutbound) {
            return !isOutbound;
        }
    }
}
