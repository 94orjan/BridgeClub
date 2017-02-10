package com.example.orjaneide.bridgeclub.util;



import com.example.orjaneide.bridgeclub.model.Club;

import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;


public class XMLParser {
    public static void readXMLFile(String filename, UserHandler handler)
            throws IOException, ParserConfigurationException, SAXException {
        File inputFile = new File(filename);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(inputFile, handler);
    }
}
