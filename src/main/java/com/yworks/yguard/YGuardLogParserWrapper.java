package com.yworks.yguard;

import org.xml.sax.SAXException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

public class YGuardLogParserWrapper extends YGuardLogParser {
    @Override
    public String[] translate(String[] args) {
        return super.translate(args);
    }

    @Override
    public void parse(File file) throws ParserConfigurationException, SAXException, IOException {
        super.parse(file);
    }
    
    @Override
    public DefaultMutableTreeNode getFieldNode(String cname, String fqn, boolean useMap) {
        return super.getFieldNode(cname, fqn, useMap);
    }

    public String getFieldName(Object userObject) {
        try {
            Method getName = Class.forName("com.yworks.yguard.YGuardLogParser$AbstractMappedStruct").getDeclaredMethod("getName");
            return (String) getName.invoke(userObject);
        } catch (Exception e) {
            return userObject.toString();
        }
    }
}
