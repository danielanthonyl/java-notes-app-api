package com.notesapp.src;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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

    public List<String> findAll(String sql) {
        start();

        try {
            Statement statement = connection.createStatement();
            Boolean hasResultSetObject = statement.execute(sql);
            List<String> results = new ArrayList<>();

            if (hasResultSetObject) {
                ResultSet resultSet = statement.getResultSet();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (resultSet.next()) {
                    String result = "";

                    for (int column = 1; column <= columnCount; column++) {
                        String columnName = String.format("\"%s\"", metaData.getColumnName(column).toLowerCase());
                        String columnValue = String.format("\"%s\"", resultSet.getString(columnName));
                        result += String.format("%s: %s", columnName, columnValue);
                        result += column == columnCount ? "" : ", ";
                    }

                    results.add(String.format("{%s}", result));
                }

                System.out.println(String.format("\n QUERY RESULT: \n %s \n", results.toString()));
                resultSet.close();
            }
            System.out.println(String.format("\n called sql: %s \n", sql));

            statement.close();
            return results;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close();
        }
    }

    public String findUnique(String sql) {
        start();

        try {
            Statement statement = connection.createStatement();
            Boolean hasResultSetObject = statement.execute(sql);
            String result = "";

            if (hasResultSetObject) {
                ResultSet resultSet = statement.getResultSet();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                if (resultSet.next()) {
                    for (int column = 1; column <= columnCount; column++) {
                        String columnName = String.format("\"%s\"", metaData.getColumnName(column).toLowerCase());
                        String columnValue = String.format("\"%s\"", resultSet.getString(columnName));
                        result += String.format("%s: %s", columnName, columnValue);
                        result += column == columnCount ? "" : ",";
                    }
                }

                result = String.format("{%s}", result);

                System.out.println(String.format("\n QUERY RESULT: \n %s \n", result));
                resultSet.close();
            }
            System.out.println(String.format("\n called sql: %s \n", sql));

            statement.close();
            return result;
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
