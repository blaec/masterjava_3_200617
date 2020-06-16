package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;
import j2html.TagCreator;
import j2html.tags.ContainerTag;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.User;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;
import static javax.management.Query.attr;

public class MainXmlJaxb {
    private static final JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);

    public static void main(String[] args) {
        String project = "topjava";
//        String project = "masterjava";
        try {
            Payload payload = JAXB_PARSER.unmarshal(Resources.getResource("payload.xml").openStream());
            List<User> userList = payload.getUsers().getUser();
            List<User> users = userList.stream()
                    .filter(u -> u.getGroup().stream().anyMatch(g -> g.getId().contains(project)))
                    .sorted(Comparator.comparing(User::getFullName))
                    .collect(Collectors.toList());
            users.forEach(u -> System.out.println(u.getFullName()));
            System.out.println(createHtmlTable(users.stream().map(User::getFullName).collect(Collectors.toList())));
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
    }

    public static String createHtmlTable(List<String> users) {
        ContainerTag html =
                html(
                        head(
                                title("Title")
                        ),
                        body(
                                table(
                                        thead(
                                                tr(
                                                        th(
                                                                "Users"
                                                        )
                                                )
                                        ),
                                        tbody(
                                                each(users, i -> tr(
                                                        td(
                                                                i
                                                        ))
                                                )
                                        )
                                )
                        )
                );


        return html.render();
    }
}
