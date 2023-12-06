package com.notesapp.utils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Utils {
    public static Map<String, String> queryParser(String query) {
        if (query == null || !query.contains("="))
            return null;

        String[] pairs = query.split("&");
        Map<String, String> queryMap = new HashMap<>();

        for (String pair : pairs) {
            String[] subPairs = pair.split("=");
            queryMap.put(subPairs[0], subPairs[1]);
        }

        return queryMap;
    }

    public String generateJson(
            int columnCount,
            ResultSet resultSet,
            ResultSetMetaData metaData) throws SQLException {
        String result = "";

        for (int column = 1; column <= columnCount; column++) {
            String columnName = String.format("\"%s\"",
                    metaData.getColumnName(column).toLowerCase());
            String columnValue = String.format("\"%s\"",
                    resultSet.getString(columnName));
            result += String.format("%s: %s", columnName, columnValue);
            result += column == columnCount ? "" : ",";
        }

        return result;
    }

    public static String sqlFileParser(String filePath) {
        try {
            Path path = Path.of(filePath);
            List<String> lines = Files.readAllLines(path);

            lines.removeIf(line -> line.contains("--") || line.length() < 1);

            String joinedLines = String
                    .join("\n", lines)
                    .replaceAll(";", "");

            System.out.println(String.format("\n QUERY EXECUTED: \n %s \n", joinedLines));

            return joinedLines;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

   public static <T> T injectDataToInstance(Map<String, Object> data, Class<T> instanceClass) throws Exception {
        Constructor<T> constructor = instanceClass.getDeclaredConstructor();
        T instance = constructor.newInstance();
        Field[] fields = instance.getClass().getDeclaredFields();

        for (int index = 0; index < fields.length; index++) {
            Field field = fields[index];
            Object dataValue = data.get(field.getName());

            if (dataValue != null) {
                field.set(instance, dataValue);
            }
        }

        return instance;
    }
}
