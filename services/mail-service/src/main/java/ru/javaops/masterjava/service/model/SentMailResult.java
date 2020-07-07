package ru.javaops.masterjava.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.service.dao.MailDao;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentMailResult {
    private final MailDao mailDao = DBIProvider.getDao(MailDao.class);
    private @NonNull LocalDateTime date;
    private @NonNull String result;
    private @NonNull String emails;

    public void save(){
        mailDao.insert(this);
    }
}
