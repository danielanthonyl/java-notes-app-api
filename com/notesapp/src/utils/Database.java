package com.notesapp.src.utils;

import java.lang.reflect.Array;
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

    public final <T> T[] findAll(String sql, Class<T> instanceClass) throws Exception {
        start();

        Statement statement = connection.createStatement();
        Boolean hasResultSetObject = statement.execute(sql);
        List<T> results = new ArrayList<T>();
        int rowCount = 0;

        if (hasResultSetObject) {
            ResultSet resultSet = statement.getResultSet();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            Map<String, Object> result = new HashMap<String, Object>();
            

            while (resultSet.next()) {
                rowCount++;
                for (int column = 1; column <= columnCount; column++) {
                    String columnName = metaData.getColumnName(column).toLowerCase();
                    Object columnValue = resultSet.getObject(column);
                    System.out.println(columnName);

                    try {
                        result.put(columnName, UUID.fromString(columnValue.toString()));
                    } catch (Exception e) {
                        result.put(columnName, columnValue);
                    }
                }

                T classInstance = Utils.injectDataToInstance(result, instanceClass);
                results.add(classInstance);
            }

            resultSet.close();
        }

        System.out.println(String.format("\n called sql: %s \n", sql));

        statement.close();
        close();

        @SuppressWarnings("unchecked")
        T[] resultList = results.toArray((T[]) Array.newInstance(instanceClass, rowCount));

        return resultList;
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

            T instance = Utils.injectDataToInstance(result, classInstance);

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
            String sqlContent = Utils.sqlFileParser(sqlFilePath);

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
}
