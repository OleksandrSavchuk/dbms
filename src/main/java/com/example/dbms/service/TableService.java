package com.example.dbms.service;

import com.example.dbms.model.Column;
import com.example.dbms.model.Table;

import java.util.List;

public interface TableService {

    Table createTable(String dbName, String tableName, List<String> columnNames, List<String> columnTypes);

    Table createTableWithColumns(String dbName, String tableName, List<Column> columns);

    void deleteTable(String dbName, String tableName);

    List<Table> listTables(String dbName);

    Table getTable(String dbName, String tableName);

}
