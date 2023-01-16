package com.example.technicaltest.service.impl;

import com.example.technicaltest.service.LogParserService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LogParserServiceImpl implements LogParserService {
    private final static String DEFAULT_FILE_EXTENSION = "log";
    private final static String NEW_FILE_NAME_SUFFIX = "-masked-sensitive-information.log";
    private final static String CARD_MASK = "************$1";
    private final static Pattern CARD_PATTERN = Pattern.compile("[0-9]{12}([0-9]{4})");

    @Override
    public String parseLog(final String fileName) {
        checkValidFileExtension(fileName);
        final File file = new File(fileName);
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            final FileInputStream fileInputStream = new FileInputStream(file);
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String strLine;
            while ((strLine = bufferedReader.readLine()) != null) {
                Matcher matcher = CARD_PATTERN.matcher(strLine);
                if (matcher.find()) {
                    stringBuilder.append(matcher.replaceAll(CARD_MASK)).append(System.lineSeparator());
                    continue;
                }
                stringBuilder.append(strLine).append(System.lineSeparator());
            }
            fileInputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final String newFileName = String.format("%s%s", FilenameUtils.removeExtension(fileName), NEW_FILE_NAME_SUFFIX);
        final String textToWrite = stringBuilder.toString();

        System.out.println(textToWrite);
        createAndWriteOutputFile(newFileName, textToWrite);
        return textToWrite;
    }

    private void checkValidFileExtension(final String fileName) {
        final String extension = FilenameUtils.getExtension(fileName);
        if (!DEFAULT_FILE_EXTENSION.equalsIgnoreCase(extension)) {
            throw new RuntimeException("Invalid type of log file!");
        }
    }

    private void createAndWriteOutputFile(final String fileName, final String text) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            fileOutputStream.write(text.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
