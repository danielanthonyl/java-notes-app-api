package com.notesapp.src;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Database {
    private String url;
    private String username;
    private String password;
    private Connection connection;

    public Database() {
        this.url = "jdbc:oracle:thin:@localhost:1521:ntsdb";
        this.username = "woedarc";
        this.password = "3306";

        this.connection = null;
    }

    public void start() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Connecting to oracle database...");

            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to Oracle database.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Connection with oracle database is closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void create(String sql) {
        start();
        System.out.println(sql);

        try {
            Statement statement = connection.createStatement();
            statement.execute(sql);

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public final <T> List<T> findAll(String sql, Class<T> instanceClass) throws Exception {
        start();

        Statement statement = connection.createStatement();
        Boolean hasResultSetObject = statement.execute(sql);
        List<T> results = new ArrayList<T>();

        if (hasResultSetObject) {
            ResultSet resultSet = statement.getResultSet();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            Map<String, Object> result = new HashMap<String, Object>();

            while (resultSet.next()) {
                for (int column = 1; column < columnCount; column++) {
                    String columnName = metaData.getColumnName(column).toLowerCase();
                    Object columnValue = resultSet.getObject(column);

                    try {
                        result.put(columnName, UUID.fromString(columnValue.toString()));
                    } catch (Exception e) {
                        result.put(columnName, columnValue);
                    }
                }

                T classInstance = injectDataToInstance(result, instanceClass);
                results.add(classInstance);
            }

            resultSet.close();
        }

        System.out.println(String.format("\n called sql: %s \n", sql));

        statement.close();
        close();

        return results;
    }

    public <T> T findUnique(String sql, Class<T> classInstance) throws Exception {
        start();

        try {
            Statement statement = connection.createStatement();
            Boolean hasResultSetObject = statement.execute(sql);
            Map<String, Object> result = new HashMap<String, Object>();

            if (hasResultSetObject) {
                ResultSet resultSet = statement.getResultSet();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                if (resultSet.next()) {
                    for (int column = 1; column < columnCount; column++) {
                        String columnName = metaData.getColumnName(column).toLowerCase();
                        Object columnValue = resultSet.getObject(column);

                        try {
                            result.put(columnName, UUID.fromString(columnValue.toString()));
                        } catch (Exception e) {
                            result.put(columnName, columnValue);
                        }
                    }
                }

                System.out.println(String.format("\n QUERY RESULT: \n %s \n", result));
                resultSet.close();
            }

            T instance = injectDataToInstance(result, classInstance);

            System.out.println(String.format("\n called sql: %s \n", sql));

            statement.close();
            return instance;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close();
        }
    }

    public void update(String sql) {
        start();

        try {
            Statement statement = connection.createStatement();
            statement.execute(sql);

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public void delete(String sql) throws SQLException {
        start();

        Statement statement = connection.createStatement();
        statement.execute(sql);
        statement.close();

        close();
    }

    public void executeQueryFromFilePath(String sqlFilePath) {
        start();

        try {
            String sqlContent = sqlFileParser(sqlFilePath);

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlContent);

            while (resultSet.next()) {
                String result = resultSet.getString("username");
                System.out.println(String.format("\n QUERY RESULT: \n %s \n", result));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    /*
     * Private helper methods
     */
    private <T> T injectDataToInstance(Map<String, Object> data, Class<T> instanceClass) throws Exception {
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

    // private String generateJson(int columnCount, ResultSet resultSet, ResultSetMetaData metaData) throws SQLException {
    //     String result = "";

    //     for (int column = 1; column <= columnCount; column++) {
    //         String columnName = String.format("\"%s\"", metaData.getColumnName(column).toLowerCase());
    //         String columnValue = String.format("\"%s\"", resultSet.getString(columnName));
    //         result += String.format("%s: %s", columnName, columnValue);
    //         result += column == columnCount ? "" : ",";
    //     }

    //     return result;
    // }

    private String sqlFileParser(String filePath) {
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
}
