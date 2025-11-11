package com.example.dbms.service;

import com.example.dbms.model.Column;

import java.util.List;

public interface ColumnService {

    Column addColumn(String dbName, String tableName, Column column);

    Column updateColumn(String dbName, String tableName, String columnName, Column column);

    void deleteColumn(String dbName, String tableName, String columnName);

    List<Column> listColumns(String dbName, String tableName);

}
