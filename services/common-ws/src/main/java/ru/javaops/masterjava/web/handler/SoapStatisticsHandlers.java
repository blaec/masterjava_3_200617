package ru.javaops.masterjava.web.handler;

import com.sun.xml.ws.api.handler.MessageHandlerContext;
import ru.javaops.masterjava.web.Statistics;

public class SoapStatisticsHandlers extends SoapBaseHandler {
    public static final String START_TIME = "start.time";
    public static final String PAYLOAD = "payload";

    @Override
    public boolean handleMessage(MessageHandlerContext context) {
        if (isOutbound(context)) {
            Statistics.count((String)context.get(PAYLOAD), (Long)context.get(START_TIME), Statistics.RESULT.SUCCESS);
        } else {
            context.put(START_TIME, System.currentTimeMillis());
            context.put(PAYLOAD, context.getMessage().getPayloadLocalPart());
        }
        return true;
    }

    @Override
    public boolean handleFault(MessageHandlerContext context) {
        Statistics.count((String)context.get(PAYLOAD), (Long)context.get(START_TIME), Statistics.RESULT.FAIL);
        return true;
    }
}
