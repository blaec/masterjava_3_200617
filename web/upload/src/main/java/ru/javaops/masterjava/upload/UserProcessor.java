package ru.javaops.masterjava.upload;

import ru.javaops.masterjava.model.BatchResult;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserFlag;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.JaxbUnmarshaller;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;

public class UserProcessor {
    private static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
    UserDao dao = DBIProvider.getDao(UserDao.class);
    private final ExecutorService userExecutor = Executors.newFixedThreadPool(8);

    public List<BatchResult> process(final InputStream is, int chunkSize) throws XMLStreamException, JAXBException {
        final StaxStreamProcessor processor = new StaxStreamProcessor(is);
        final CompletionService<String> completionService = new ExecutorCompletionService<>(userExecutor);
        List<Future<String>> futureList = new ArrayList<>();

        JaxbUnmarshaller unmarshaller = jaxbParser.createUnmarshaller();
        List<User> users = new ArrayList<>();
        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            ru.javaops.masterjava.xml.schema.User xmlUser = unmarshaller.unmarshal(processor.getReader(), ru.javaops.masterjava.xml.schema.User.class);
            final User user = new User(xmlUser.getValue(), xmlUser.getEmail(), UserFlag.valueOf(xmlUser.getFlag().value()));
            users.add(user);
            if (users.size() == chunkSize) {
                futureList.add(createFuture(completionService, users, chunkSize));
                users = new ArrayList<>();
            }
        }
        if (users.size() > 0) {
            futureList.add(createFuture(completionService, users, chunkSize));
        }

        List<BatchResult> result = new ArrayList<>();
        while (!futureList.isEmpty()) {
            BatchResult batchResult = null;
            try {
                Future<String> future;
                if ((future = completionService.poll(10, TimeUnit.SECONDS)) != null) {
                    futureList.remove(future);
                    batchResult = new BatchResult(future.get(), "Unique email constraint violation");
                }
            } catch (InterruptedException e) {
                batchResult = new BatchResult("InterruptedException", e.getMessage());
            } catch (ExecutionException e) {
                batchResult = new BatchResult("ExecutionException", e.getMessage());
            }
            if (batchResult != null) {
                result.add(batchResult);
            }
        }

        return result;
    }

    private Future<String> createFuture(CompletionService<String> completionService, List<User> users, int chunkSize) {
        return completionService.submit(() -> {
            int[] ids = dao.insertBatch(users, chunkSize);
            List<User> failedUsers = new ArrayList<>();
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] == 0) {
                    failedUsers.add(users.get(i));
                }
            }
            return getEmailRange(failedUsers);
        });
    }

    private String getEmailRange(List<User> users) {
        return String.format("%s-%s", users.get(0).getEmail(), users.get(users.size() - 1).getEmail());
    }
}
