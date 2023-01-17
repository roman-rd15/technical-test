package com.example.technicaltest;

import com.example.technicaltest.service.LogParserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LogParserServiceTest {
    private final static String INVALID_FILE_FORMAT_NAME = "application-test.doc";
    private final static String VALID_FILE_FORMAT_NAME = "application-test.log";
    private final static String SUCCESSFULLY_CREATED_FILE_NAME = "application-test-masked-sensitive-information.log";
    private final static Pattern MASKED_CARD_PATTERN = Pattern.compile("([*]{12}([0-9]{4}))");
    private final static Pattern NOT_MASKED_CARD_PATTERN = Pattern.compile("[0-9]{12}([0-9]{4})");
    private final static String EXPECTED_RESPONSE_FILE = "application-test-expected-response-file.log";

    @Autowired
    private LogParserService logParserService;

    private String expectedResult;

    @BeforeEach
    public void setUp() {
        final File file = new File(EXPECTED_RESPONSE_FILE);
        this.expectedResult = readFileContent(file);
    }

    @Test
    public void shouldThrowExceptionIfFileExtensionIsNotLog() {
        assertThrows(RuntimeException.class, () -> {
            logParserService.parseLog(INVALID_FILE_FORMAT_NAME);
        }, "Invalid type of log file!");
    }

    @Test
    public void shouldReturnSuccessResult() {
        final String result = logParserService.parseLog(VALID_FILE_FORMAT_NAME);

        final Matcher maskedCardsMatcher = MASKED_CARD_PATTERN.matcher(result);
        final List<String> maskedCardsMatcherResults = maskedCardsMatcher.results().map(MatchResult::group).toList();

        assertEquals(expectedResult, result);

        assertFalse(result.isEmpty());
        assertFalse(maskedCardsMatcherResults.isEmpty());
        assertEquals(2, maskedCardsMatcherResults.size());

        final Matcher notMaskedCardsMatcher = NOT_MASKED_CARD_PATTERN.matcher(result);
        final List<String> notMaskedCardsMatcherResults = notMaskedCardsMatcher.results().map(MatchResult::group).toList();

        assertTrue(notMaskedCardsMatcherResults.isEmpty());
    }

    @Test
    public void successfullyCreatedResponseFile() {
        final String result = logParserService.parseLog(VALID_FILE_FORMAT_NAME);
        final File file = new File(SUCCESSFULLY_CREATED_FILE_NAME);

        assertTrue(file.exists());

        final String createdFileContent = readFileContent(file);
        assertEquals(expectedResult, createdFileContent);
    }

    private String readFileContent(final File file) {
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            final FileInputStream fileInputStream = new FileInputStream(file);
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            String strLine;
            while ((strLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(strLine).append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return stringBuilder.toString();
    }
}
