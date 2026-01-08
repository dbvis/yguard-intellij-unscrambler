package com.dbvis.yguard;

import com.intellij.openapi.project.Project;
import com.intellij.unscramble.UnscrambleSupport;
import com.yworks.yguard.YGuardLogParserWrapper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;


public class YGuardUnscrambler implements UnscrambleSupport<JComponent> {
    private static final Pattern npePattern = Pattern.compile("(?<prefix>.*)Cannot invoke \"(?<method>.*)\" (?<reason>because[^\"]*)\"(?<this>this\\.)?(?<field>.*)\" is null(?<suffix>.*)");
    private static final Pattern atPattern = Pattern.compile("at (.+)\\.[^.]+\\(");
    
    @Override
    public @NotNull String getPresentableName() {
        return "yGuard Unscrambler";
    }

    @Override
    public @Nullable String unscramble(@NotNull Project project, @NotNull String text, @NotNull String logName, @Nullable JComponent settings) {
        File logFile = new File(logName);
        if (logName.isBlank() || !logFile.exists()) {
            return null;
        }

        YGuardLogParserWrapper parser = new YGuardLogParserWrapper();
        try {
            parser.parse(logFile);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            return "Error parsing yGuard logfile: " + e.getMessage();
        }
        String[] split = text.split("\\n");
        List<String> translatedLines = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            String in = split[i];
            Matcher npeMatcher = npePattern.matcher(in);
            String out = parser.translate(new String[]{in})[0];
            if (npeMatcher.matches()) {
                String nullField = npeMatcher.group("field");
                if (npeMatcher.group("this") != null) {
                    if (i < split.length - 1) {
                        Matcher nextMatcher = atPattern.matcher(split[i + 1]);
                        if (nextMatcher.find()) {
                            String translatedClass = parser.translate(nextMatcher.group(1));
                            DefaultMutableTreeNode fieldNode = parser.getFieldNode(translatedClass, npeMatcher.group("field"), true);
                            Object userObject = fieldNode.getUserObject();
                            try {
                                Method getName = Class.forName("com.yworks.yguard.YGuardLogParser$AbstractMappedStruct").getDeclaredMethod("getName");
                                nullField = (String) getName.invoke(userObject);
                            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                                     ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                } else {
                    nullField = parser.translate(new String[]{npeMatcher.group("field")})[0];
                }

                out = String.format("%sCannot invoke \"%s\" %s\"%s%s\" is null%s",
                        npeMatcher.group("prefix"),
                        parser.translate(new String[]{npeMatcher.group("method")})[0],
                        npeMatcher.group("reason"),
                        npeMatcher.group("this") == null ? "" : npeMatcher.group("this"),
                        nullField,
                        npeMatcher.group("suffix"));
            }
            translatedLines.add(out);
        }

        return StringUtils.join(translatedLines, '\n');
    }
}
