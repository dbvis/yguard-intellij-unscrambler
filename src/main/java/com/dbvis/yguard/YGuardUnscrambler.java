package com.dbvis.yguard;

import com.intellij.openapi.project.Project;
import com.intellij.unscramble.UnscrambleSupport;
import com.yworks.yguard.YGuardLogParserWrapper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;


public class YGuardUnscrambler implements UnscrambleSupport<JComponent> {
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

        return StringUtils.join(parser.translate(text.split("\\n")), '\n');
    }
}
