package ru.fizteh.fivt.students.w4r10ck1337.miniorm;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.CaseFormat;
import ru.fizteh.fivt.students.w4r10ck1337.miniorm.annotations.Column;
import ru.fizteh.fivt.students.w4r10ck1337.miniorm.annotations.PrimaryKey;
import ru.fizteh.fivt.students.w4r10ck1337.miniorm.annotations.Table;


import java.lang.reflect.Field;
import java.util.*;

public class DatabaseService<K, T> {
    private Map<K, T> database;
    private String tableName;

    private List<String> columnNames = new ArrayList<>();
    private List<Field> columns = new ArrayList<>();
    private Field primaryKeyField = null;


    public DatabaseService(Class<T> clazz) {
        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new IllegalArgumentException("Class is not a table");
        }
        tableName = clazz.getAnnotation(Table.class).name();
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
                }
            }

            columnNames.add(name);
            columns.add(column);
        }

        if (primaryKeyField == null) {
            throw new IllegalArgumentException("No primary key");
        }
    }

    @SuppressWarnings("unchecked")
    private K getPrimaryKey(T element) {
        try {
            return (K) primaryKeyField.get(element);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public T queryById(K key) {
        return database.get(key);
    }

    public List<T> queryForAll() {
        return Lists.newArrayList(database.values());
    }

    public void insert(T element) {
        database.put(getPrimaryKey(element), element);
    }

    public void update(T element) {
        database.replace(getPrimaryKey(element), element);
    }

    public void delete(T element) {
        database.remove(getPrimaryKey(element));
    }

    public void createTable() {
        database = new TreeMap<>();
    }

    public void dropTable() {
        database.clear();
    }
}
