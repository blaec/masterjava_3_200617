package ru.javaops.masterjava.upload;

import org.thymeleaf.context.WebContext;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static ru.javaops.masterjava.common.web.ThymeleafListener.engine;

@WebServlet(urlPatterns = "/", loadOnStartup = 1)
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10) //10 MB in memory limit
public class UploadServlet extends HttpServlet {

    private final UserProcessor userProcessor = new UserProcessor();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale());
        engine.process("upload", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale());

        try {
//            http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html
            Part filePart = req.getPart("fileToUpload");
            if (filePart.getSize() == 0) {
                throw new IllegalStateException("Upload file have not been selected");
            }
            try (InputStream is = filePart.getInputStream()) {

                // save users to db
                List<User> users = userProcessor.process(is);
                int chunkSize = Integer.parseInt(req.getParameter("chunkSize"));
                UserDao dao = DBIProvider.getDao(UserDao.class);
                DBIProvider.getDBI().useTransaction((conn, status) -> {
                    int[] ids = dao.insertAll(users, chunkSize);
                });

                // get 20 last users and display them
                webContext.setVariable("users", dao.getFirstWithLimit(20));
                engine.process("result", webContext, resp.getWriter());
            }
        } catch (Exception e) {
            webContext.setVariable("exception", e);
            engine.process("exception", webContext, resp.getWriter());
        }
    }
}
