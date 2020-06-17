package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;

public class MainXlst {
    public static void main(String[] args) {
        try (InputStream xslInputStream = Resources.getResource("groups.xsl").openStream();
             InputStream xmlInputStream = Resources.getResource("payload.xml").openStream()) {
//        try (InputStream xslInputStream = Resources.getResource("test.xsl").openStream();
//             InputStream xmlInputStream = Resources.getResource("test.xml").openStream()) {

            XsltProcessor processor = new XsltProcessor(xslInputStream);
            System.out.println(processor.transform(xmlInputStream));
        } catch (TransformerException | IOException e) {
            e.printStackTrace();
        }
    }
}
