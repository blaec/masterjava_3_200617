package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.User;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Comparator;

public class MainXmlJaxb {
    private static final JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);

    public static void main(String[] args) {
//        String project = "topjava";
        String project = "masterjava";
        try {
            Payload payload = JAXB_PARSER.unmarshal(Resources.getResource("payload.xml").openStream());
            payload.getUsers().getUser().stream()
                    .filter(u -> u.getGroup().stream().anyMatch(g -> g.getId().contains(project)))
                    .sorted(Comparator.comparing(User::getFullName))
                    .forEach(u -> System.out.println(u.getFullName()));
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }

    }
}
