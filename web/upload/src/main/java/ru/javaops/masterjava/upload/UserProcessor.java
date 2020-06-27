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
import java.util.stream.Collectors;

public class UserProcessor {
    private static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
    UserDao dao = DBIProvider.getDao(UserDao.class);
    private final ExecutorService userExecutor = Executors.newFixedThreadPool(8);

    public List<BatchResult> process(final InputStream is, int chunkSize) throws XMLStreamException, JAXBException {
        final StaxStreamProcessor processor = new StaxStreamProcessor(is);
        List<User> users = new ArrayList<>();
        Map<String, List<User>> userChunks = new HashMap<>();
        final CompletionService<String> completionService = new ExecutorCompletionService<>(userExecutor);

        JaxbUnmarshaller unmarshaller = jaxbParser.createUnmarshaller();
        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            ru.javaops.masterjava.xml.schema.User xmlUser = unmarshaller.unmarshal(processor.getReader(), ru.javaops.masterjava.xml.schema.User.class);
            final User user = new User(xmlUser.getValue(), xmlUser.getEmail(), UserFlag.valueOf(xmlUser.getFlag().value()));
            users.add(user);
            if (users.size() == chunkSize) {
                String key = String.format("%s-%s", users.get(0).getEmail(), user.getEmail());
                userChunks.put(key, users);
                users = new ArrayList<>();
            }
        }
        if (users.size() > 0) {
            String key = String.format("%s-%s", users.get(0).getEmail(), users.get(users.size() - 1).getEmail());
            userChunks.put(key, users);
        }

        List<Future<String>> futureList = new ArrayList<>();
        for (String key : userChunks.keySet()) {
            Future<String> failedChunk = completionService.submit(() -> {
                List<User> userList = userChunks.get(key);
                int[] ids = dao.insertBatch(userList, chunkSize);
                List<User> failedUsers = new ArrayList<>();
                for (int i = 0; i < ids.length; i++) {
                    if (ids[i] == 0) {
                        failedUsers.add(userList.get(i));
                    }
                }
                return String.format("%s-%s", failedUsers.get(0).getEmail(), failedUsers.get(failedUsers.size() - 1).getEmail());
            });
            futureList.add(failedChunk);
        }

        List<BatchResult> result = new ArrayList<>();
        while (!futureList.isEmpty()) {
            try {
                Future<String> future = completionService.poll(10, TimeUnit.SECONDS);
                if (future == null) {
                    continue;
                }
                futureList.remove(future);
                String failedUsers = future.get();
                BatchResult batchResult = new BatchResult(failedUsers, "Unique email constraint violation");
                result.add(batchResult);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return result;
    }


}
