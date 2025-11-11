package com.example.dbms.service;

import com.example.dbms.model.Table;

public interface TableOperationService {

    Table unionTables(String dbName, String table1Name, String table2Name, String resultTableName);

}
