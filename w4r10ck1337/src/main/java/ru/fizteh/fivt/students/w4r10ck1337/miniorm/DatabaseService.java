package ru.fizteh.fivt.students.w4r10ck1337.miniorm;

import com.google.common.base.CaseFormat;
import ru.fizteh.fivt.students.w4r10ck1337.miniorm.annotations.Column;
import ru.fizteh.fivt.students.w4r10ck1337.miniorm.annotations.PrimaryKey;
import ru.fizteh.fivt.students.w4r10ck1337.miniorm.annotations.Table;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService<K, T> {
    private static String databasePath = "./db";

    private Connection connection;
    private String valuesPattern = "";

    private String tableName;
    private Class<T> tableClass;

    private List<String> columnNames = new ArrayList<>();
    private List<Field> columns = new ArrayList<>();
    private Field primaryKeyField = null;
    private int primaryKeyFieldNumber = -1;

    public DatabaseService(Class<T> clazz) throws DatabaseException {
        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new IllegalArgumentException("Class is not a table");
        }
        tableName = clazz.getAnnotation(Table.class).name();
        tableClass = clazz;
        if (tableName.equals("")) {
            tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getSimpleName());
        }

        for (Field column : clazz.getDeclaredFields()) {
            if (!column.isAnnotationPresent(Column.class)) {
                throw new IllegalArgumentException("Some field is not a column");
            }

            String name = column.getAnnotation(Column.class).name();
            if (name.equals("")) {
                name = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, column.getName());
            }

            if (column.isAnnotationPresent(PrimaryKey.class)) {
                if (primaryKeyField != null) {
                    throw new IllegalArgumentException("Primary key is not unique");
                } else {
                    primaryKeyField = column;
                    primaryKeyFieldNumber = columns.size();
                }
            }

            columnNames.add(name);
            columns.add(column);
            valuesPattern += "?,";
        }
        valuesPattern = valuesPattern.substring(0, valuesPattern.length() - 1);

        if (primaryKeyField == null) {
            throw new IllegalArgumentException("No primary key");
        }


        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:" + databasePath, "sa", "");
        } catch (Exception e) {
            throw new DatabaseException("Can't connect to database");
        }
    }

    public static void setDatabasePath(String path) {
        databasePath = path;
    }

    public static String getDatabasePath() {
        return databasePath;
    }

    @SuppressWarnings("unchecked")
    private K getPrimaryKey(T element) throws DatabaseException {
        try {
            return (K) primaryKeyField.get(element);
        } catch (IllegalAccessException e) {
            throw new DatabaseException("Can't get primary key");
        }
    }

    private String prepareColumnNames(String pattern) {
        StringBuilder result = new StringBuilder();
        for (String name : columnNames) {
            result.append(name).append(pattern);
        }
        return result.substring(0, result.length() - 1);
    }

    private String getClassName(Class clazz) throws DatabaseException {
        if (clazz == Integer.class) {
            return "INTEGER";
        } else if (clazz == Boolean.class) {
            return "BOOLEAN";
        } else if (clazz == Byte.class) {
            return "TINYINT";
        } else if (clazz == Short.class) {
            return "SMALLINT";
        } else if (clazz == Long.class) {
            return "BIGINT";
        } else if (clazz == Double.class) {
            return "DOUBLE";
        } else if (clazz == Float.class) {
            return "FLOAT";
        } else if (clazz == Character.class) {
            return "CHAR";
        } else if (clazz == String.class) {
            return "CLOB";
        } else {
            throw new DatabaseException("Class is not supported");
        }
    }

    private List<T> convertQueryResult(ResultSet queryResult) throws Exception {
        List<T> result = new ArrayList<>();
        while (queryResult.next()) {
            T object = tableClass.newInstance();
            for (int i = 0; i < columns.size(); ++i) {
                String value = queryResult.getString(i + 1);
                if (columns.get(i).getType().equals(String.class)) {
                    columns.get(i).set(object, value);
                } else {
                    columns.get(i).set(
                            object,
                            columns.get(i).getType().getMethod("valueOf", String.class).invoke(null, value));
                }
            }
            result.add(object);
        }
        queryResult.close();
        return result;
    }

    public T queryById(K key) throws DatabaseException {
        try {
            PreparedStatement query = connection.prepareStatement(
                    "SELECT " + prepareColumnNames(",")
                    + " FROM " + tableName + " WHERE " + columnNames.get(primaryKeyFieldNumber) + "=?");
            query.setString(1, key.toString());
            List<T> result = convertQueryResult(query.executeQuery());
            if (!result.isEmpty()) {
                return result.get(0);
            }
            return null;
        } catch (Exception e) {
            throw new DatabaseException("Query by id fail");
        }
    }

    public List<T> queryForAll() throws DatabaseException {
        try {
            PreparedStatement query = connection.prepareStatement(
                    "SELECT " + prepareColumnNames(",")
                    + " FROM " + tableName);
            return convertQueryResult(query.executeQuery());
        } catch (Exception e) {
            throw new DatabaseException("Query for all fail");
        }
    }

    public void insert(T element) throws DatabaseException {
        try {
            PreparedStatement query = connection.prepareStatement(
                    "INSERT INTO " + tableName
                    + " VALUES (" + valuesPattern + ")");
            for (int i = 0; i < columns.size(); ++i) {
                query.setString(i + 1, columns.get(i).get(element).toString());
            }
            query.execute();
        } catch (Exception e) {
            throw new DatabaseException("Insert object fail");
        }
    }

    public void update(T element) throws DatabaseException {
        try {
            PreparedStatement query = connection.prepareStatement(
                    "UPDATE " + tableName
                    + " SET " + prepareColumnNames("=?,")
                    + " WHERE " + columnNames.get(primaryKeyFieldNumber) + "=?");
            for (int i = 0; i < columns.size(); ++i) {
                query.setString(i + 1, (columns.get(i).get(element)).toString());
            }
            query.setString(columns.size() + 1, getPrimaryKey(element).toString());
            query.executeUpdate();
        } catch (Exception e) {
            throw new DatabaseException("Update object fail");
        }
    }

    public void delete(T element) throws DatabaseException {
        try {
            PreparedStatement query = connection.prepareStatement(
                    "DELETE FROM " + tableName
                    + " WHERE " + columnNames.get(primaryKeyFieldNumber) + "=?");
            query.setString(1, getPrimaryKey(element).toString());
            query.execute();
        } catch (Exception e) {
            throw new DatabaseException("Delete object fail");
        }
    }

    public void createTable() throws DatabaseException {
        try {
            StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + "(");
            for (int i = 0; i < columns.size(); i++) {
                String type = getClassName(columns.get(i).getType());
                if (i == primaryKeyFieldNumber) {
                    type += " PRIMARY KEY NOT NULL";
                }
                query.append(columnNames.get(i)).append(" ").append(type).append(",");
            }
            connection.createStatement().execute(query.substring(0, query.length() - 1) + ")");
        } catch (Exception e) {
            throw new DatabaseException("Create table fail");
        }
    }

    public void dropTable() throws DatabaseException {
        try {
            PreparedStatement query = connection.prepareStatement(
                    "DROP TABLE IF EXISTS " + tableName);
            query.execute();
        } catch (Exception e) {
            throw new DatabaseException("Drop table fail");
        }
    }

    @Override
    protected final void finalize() {
        try {
            connection.close();
        } catch (Exception e) {
            System.err.println("Close connection fail");
        }
    }
}
