package com.example.dbms.service.impl;

import com.example.dbms.model.Column;
import com.example.dbms.model.Database;
import com.example.dbms.model.Row;
import com.example.dbms.model.Table;
import com.example.dbms.repository.DatabaseRepository;
import com.example.dbms.service.StorageService;
import com.example.dbms.service.TableOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TableOperationServiceImpl implements TableOperationService {

    private final DatabaseRepository databaseRepository;
    private final StorageService storageService;

    @Override
    public Table unionTables(String dbName, String table1Name, String table2Name, String resultTableName) {
        Database db = databaseRepository.findByName(dbName)
                .orElseThrow(() -> new IllegalArgumentException("Database not found"));
        Table table1 = db.getTables().get(table1Name);
        Table table2 = db.getTables().get(table2Name);

        if (table1 == null || table2 == null) {
            throw new IllegalArgumentException("Table not found");
        }

        List<String> table1Columns = table1.getColumns().stream()
                .map(Column::getName).collect(Collectors.toList());
        List<String> table2Columns = table2.getColumns().stream()
                .map(Column::getName).collect(Collectors.toList());

        if (!table1Columns.equals(table2Columns)) {
            throw new IllegalArgumentException("Tables do not match");
        }

        Table result = new Table();
        result.setName(resultTableName);
        result.setColumns(new ArrayList<>(table1.getColumns()));

        Set<Map<String, Object>> uniqueRows = new LinkedHashSet<>();

        for (Row row : table1.getRows()) {
            Map<String, Object> rowWithoutId = new LinkedHashMap<>(row.getValues());
            rowWithoutId.remove("id");
            uniqueRows.add(rowWithoutId);
        }

        for (Row row : table2.getRows()) {
            Map<String, Object> rowWithoutId = new LinkedHashMap<>(row.getValues());
            rowWithoutId.remove("id");
            uniqueRows.add(rowWithoutId);
        }

        List<Row> resultRows = new ArrayList<>();
        int newId = 1;

        for (Map<String, Object> rowData : uniqueRows) {
            Map<String, Object> newRowData = new LinkedHashMap<>();

            newRowData.put("id", newId++);

            newRowData.putAll(rowData);

            resultRows.add(new Row(newRowData));
        }

        result.setRows(resultRows);
        result.setNextId(newId);

        db.getTables().put(resultTableName, result);
        databaseRepository.save(db);
        storageService.saveToDisk(databaseRepository.getAll());

        return result;
    }
}