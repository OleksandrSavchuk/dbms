package com.example.dbms.service;

import com.example.dbms.model.Row;

import java.util.List;

public interface RowService {

    Row addRow(String dbName, String tableName, Row row);

    Row updateRow(String dbName, String tableName, int rowIndex, Row row);

    void deleteRow(String dbName, String tableName, int rowIndex);

    List<Row> listRows(String dbName, String tableName);

}
