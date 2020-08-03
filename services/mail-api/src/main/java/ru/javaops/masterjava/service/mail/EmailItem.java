package ru.javaops.masterjava.service.mail;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class EmailItem implements Serializable {
    private @NotNull String users;
    private String subject;
    private @NotNull String body;
}

