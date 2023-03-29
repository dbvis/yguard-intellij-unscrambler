package com.yworks.yguard;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class YGuardLogParserWrapper extends YGuardLogParser {
    @Override
    public String[] translate(String[] args) {
        return super.translate(args);
    }

    @Override
    public void parse(File file) throws ParserConfigurationException, SAXException, IOException {
        super.parse(file);
    }
}
