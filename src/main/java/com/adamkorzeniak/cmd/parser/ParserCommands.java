package com.adamkorzeniak.cmd.parser;

import com.adamkorzeniak.file.FileUtils;
import com.adamkorzeniak.json.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@ShellComponent
public class ParserCommands {

    @Value(value = "${parser.file.location.input}")
    private String defaultInputFilePath;

    @Value(value = "${parser.file.location.output}")
    private String defaultOutputFilePath;


    @ShellMethod(
            value = "Parse and modify JSON file",
            key = "pj")
    public String sqlList(
            @ShellOption(value = {"-s", "--select"}) String selectExpression,
            @ShellOption(value = {"-f", "--filter"}) String filterExpression,
            @ShellOption(value = {"-o", "--order"}) String orderExpression
    ) {
        String jsonString = readFileContent(defaultInputFilePath);
        Map<String, Object> jsonModel = JsonUtils.buildJsonMap(jsonString);
        //Additionally - not include here
        Object object = new Object();
        Map<String, Object> jsonModelFromPojo = JsonUtils.buildJsonMap(object);
        //Example
        ObjectMapper objMapper = new ObjectMapper();
        String jsonStr = Obj.writeValueAsString(object);
        //End
        //selectExpression = id,user.contact,user.contact.phone
        //orderExpression = id,user.contact asc,user.contact.phone
        //filterExpression =
        // id eq 1,
        // id neq 2,
        // user.contact ex,
        // user.contact.phone nex,
        // user.rating gt 1,
        // user.rating lt 2za,
        // user.rating le 2,
        // user.rating ge 3
        // user.name like slaw
        // user.name nlike slaw
        JsonUtils.filterElements(jsonModel, filterExpression);
        JsonUtils.orderElements(jsonModel, orderExpression);
        JsonUtils.selectFields(jsonModel, selectExpression);
        String jsonResultString = JsonUtils.convertToString(jsonModel);
        FileUtils.outputContent(defaultOutputFilePath, jsonResultString);
        return jsonString;
    }

    private String readFileContent(String filePath) {
        try {
            return FileUtils.readFileContent(filePath);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Could not open file.%nException: %s", e));
        }
    }

}