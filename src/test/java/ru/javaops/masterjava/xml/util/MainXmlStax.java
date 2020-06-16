package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class MainXmlStax {
    public static void main(String[] args) {
        String lookForProject = args[0];
        try (StaxStreamProcessor processor =
                     new StaxStreamProcessor(Resources.getResource("payload.xml").openStream())) {
            XMLStreamReader reader = processor.getReader();
            Set<String> users = new TreeSet<>();
            boolean belongsToGroup = false;
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT) {
                    if ("User".equals(reader.getLocalName())) {
                        belongsToGroup = processor.getAttributeValue("group").contains(lookForProject);
                    } else if (belongsToGroup && "fullName".equals(reader.getLocalName())) {
                        users.add(reader.getElementText());
                        belongsToGroup = false;
                    }
                }
            }
            users.forEach(System.out::println);
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
        }
    }
}
