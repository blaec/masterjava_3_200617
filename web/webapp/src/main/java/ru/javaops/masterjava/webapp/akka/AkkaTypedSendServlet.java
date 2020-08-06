package ru.javaops.masterjava.webapp.akka;

import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.MailRemoteService;
import ru.javaops.masterjava.service.mail.util.MailUtils.MailObject;
import ru.javaops.masterjava.util.Exceptions;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.javaops.masterjava.webapp.WebUtil.*;
import static ru.javaops.masterjava.webapp.akka.AkkaWebappListener.akkaActivator;

@WebServlet(value = "/sendAkkaTyped", loadOnStartup = 1, asyncSupported = true)
@Slf4j
@MultipartConfig
public class AkkaTypedSendServlet extends HttpServlet {

    private MailRemoteService mailService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mailService = akkaActivator.getTypedRef(MailRemoteService.class, "akka.tcp://MailService@127.0.0.1:2553/user/mail-remote-service");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        // https://dzone.com/articles/limited-usefulness
        doAsync(resp, () -> {
            MailObject mailObject = createMailObject(req);

            final AsyncContext ac = req.startAsync();
            ac.start(Exceptions.<IOException>wrap(() -> {
                doAndWriteResponse((HttpServletResponse) ac.getResponse(), () -> {
                    scala.concurrent.Future<GroupResult> future = mailService.sendBulk(mailObject);
                    log.info("Receive future, await result ...");
                    GroupResult groupResult = Await.result(future, Duration.create(10, "seconds"));
                    return groupResult.toString();
                });
                ac.complete();
            }));
        });
    }
}