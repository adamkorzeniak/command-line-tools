package com.adamkorzeniak.cmd.sql;

import com.adamkorzeniak.file.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;
import java.util.stream.Collectors;

@ShellComponent
public class SQLCommands {

    private static final String STRING_PREFIX = "('";
    private static final String STRING_DELIMITER = "', '";
    private static final String STRING_SUFFIX = "')";

    private static final String NON_STRING_PREFIX = "(";
    private static final String NON_STRING_DELIMITER = ", ";
    private static final String NON_STRING_SUFFIX = ")";

    @Value(value = "${sql.file.location}")
    private String defaultFilePath;


    @ShellMethod(
            value = "Build SQL list that can be used in '... where column in {result}'",
            key = "sl")
    public String sqlList(
            @ShellOption(value = {"-s", "--string"}, defaultValue = "false") boolean isString
    ) {
        String prefix = isString ? STRING_PREFIX : NON_STRING_PREFIX;
        String delimiter = isString ? STRING_DELIMITER : NON_STRING_DELIMITER;
        String suffix = isString ? STRING_SUFFIX : NON_STRING_SUFFIX;

        try {
            return FileUtils.openFileStream(defaultFilePath)
                        .collect(Collectors.joining(delimiter, prefix, suffix));
        } catch (IOException e) {
            throw new RuntimeException(String.format("Could not open file.%nException: %s", e));
        }
    }

    @ShellMethodAvailability({"sl", "help"})
    public Availability availabilityCheck() {
        if (Math.random() > 0) {
            return Availability.available();
        } else {
            return Availability.unavailable("You had bad luck. Please, try again");
        }
    }
}