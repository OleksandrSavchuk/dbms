package com.example.dbms.service.impl;

import com.example.dbms.model.Column;
import com.example.dbms.model.DataType;
import com.example.dbms.model.Database;
import com.example.dbms.model.Table;
import com.example.dbms.repository.DatabaseRepository;
import com.example.dbms.service.ColumnService;
import com.example.dbms.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ColumnServiceImpl implements ColumnService {

    private final DatabaseRepository databaseRepository;

    private final StorageService storageService;

    @Override
    public Column addColumn(String dbName, String tableName, Column column) {
        validateType(column.getType());

        Database db = databaseRepository.findByName(dbName)
                .orElseThrow(() -> new IllegalArgumentException("Database " + dbName + " not found"));
        Table table = db.getTables().get(tableName);
        if (table == null) {
            throw new IllegalArgumentException("Table " + tableName + " not found");
        }
        if (table.getColumns().stream().anyMatch(c -> c.getName().equals(column.getName()))) {
            throw new IllegalArgumentException("Column " + column.getName() + " already exists");
        }

        table.getColumns().add(column);
        databaseRepository.save(db);
        storageService.saveToDisk(databaseRepository.getAll());

        return column;
    }

    @Override
    public Column updateColumn(String dbName, String tableName, String columnName, Column column) {
        validateType(column.getType());

        Database db = databaseRepository.findByName(dbName)
                .orElseThrow(() -> new IllegalArgumentException("Database " + dbName + " not found"));
        Table table = db.getTables().get(tableName);
        if (table == null) {
            throw new IllegalArgumentException("Table " + tableName + " not found");
        }

        Column existing = table.getColumns().stream()
                .filter(c -> c.getName().equals(columnName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Column " + columnName + " not found"));

        existing.setName(column.getName());
        existing.setType(column.getType());
        databaseRepository.save(db);
        storageService.saveToDisk(databaseRepository.getAll());
        return existing;
    }

    @Override
    public void deleteColumn(String dbName, String tableName, String columnName) {
        Database db = databaseRepository.findByName(dbName)
                .orElseThrow(() -> new IllegalArgumentException("Database " + dbName + " not found"));
        Table table = db.getTables().get(tableName);
        if (table == null) {
            throw new IllegalArgumentException("Table " + tableName + " not found");
        }

        table.getColumns().removeIf(c -> c.getName().equals(columnName));
        databaseRepository.save(db);
        storageService.saveToDisk(databaseRepository.getAll());
    }

    @Override
    public List<Column> listColumns(String dbName, String tableName) {
        Database db = databaseRepository.findByName(dbName)
                .orElseThrow(() -> new IllegalArgumentException("Database " + dbName + " not found"));
        Table table = db.getTables().get(tableName);
        if (table == null) {
            throw new IllegalArgumentException("Table " + tableName + " not found");
        }

        return new ArrayList<>(table.getColumns());
    }

    private void validateType(DataType dataType) {
        if (dataType == null)
            throw new IllegalArgumentException("Not null!!!");
    }

}
