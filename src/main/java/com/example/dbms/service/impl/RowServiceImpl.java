package com.example.dbms.service.impl;

import com.example.dbms.model.*;
import com.example.dbms.repository.DatabaseRepository;
import com.example.dbms.service.RowService;
import com.example.dbms.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RowServiceImpl implements RowService {

    private final DatabaseRepository databaseRepository;
    private final StorageService storageService;

    @Override
    public Row addRow(String dbName, String tableName, Row row) {
        Table table = getTable(dbName, tableName);

        // Автоінкремент id
        Optional<Column> idColumnOpt = table.getColumns().stream()
                .filter(c -> c.getName().equals("id") && c.getType() == DataType.INTEGER)
                .findFirst();

        if (idColumnOpt.isPresent()) {
            // Генеруємо унікальний ID
            int newId = table.getNextId();

            // Перевіряємо чи ID вже існує (на всяк випадок)
            while (isIdExists(table, newId)) {
                newId++;
            }

            row.getValues().put("id", newId);
            table.setNextId(newId + 1);
        }

        validateRow(row, table);

        table.getRows().add(row);
        saveDatabase(dbName);
        return row;
    }

    @Override
    public Row updateRow(String dbName, String tableName, int rowIndex, Row row) {
        Table table = getTable(dbName, tableName);

        if (rowIndex < 0 || rowIndex >= table.getRows().size())
            throw new IllegalArgumentException("Індекс рядка поза межами");

        validateRow(row, table);

        // Перевірка унікальності ID при оновленні
        Object newIdValue = row.getValues().get("id");
        if (newIdValue instanceof Integer) {
            int newId = (Integer) newIdValue;

            // Перевіряємо чи такий ID вже є в інших рядках
            for (int i = 0; i < table.getRows().size(); i++) {
                if (i == rowIndex) continue; // пропускаємо поточний рядок

                Object existingId = table.getRows().get(i).getValues().get("id");
                if (existingId instanceof Integer && (Integer) existingId == newId) {
                    throw new IllegalArgumentException("Рядок з ID=" + newId + " вже існує");
                }
            }
        }

        table.getRows().set(rowIndex, row);
        saveDatabase(dbName);
        return row;
    }

    @Override
    public void deleteRow(String dbName, String tableName, int rowIndex) {
        Table table = getTable(dbName, tableName);

        if (rowIndex < 0 || rowIndex >= table.getRows().size())
            throw new IllegalArgumentException("Індекс рядка поза межами");

        table.getRows().remove(rowIndex);
        saveDatabase(dbName);
    }

    @Override
    public List<Row> listRows(String dbName, String tableName) {
        Table table = getTable(dbName, tableName);
        return new ArrayList<>(table.getRows());
    }

    // --- допоміжні методи ---

    private Table getTable(String dbName, String tableName) {
        Database db = databaseRepository.findByName(dbName)
                .orElseThrow(() -> new IllegalArgumentException("База '" + dbName + "' не знайдена"));
        Table table = db.getTables().get(tableName);
        if (table == null) throw new IllegalArgumentException("Таблиця '" + tableName + "' не знайдена");
        return table;
    }

    /**
     * Перевіряє чи ID вже існує в таблиці
     */
    private boolean isIdExists(Table table, int id) {
        for (Row existingRow : table.getRows()) {
            Object existingId = existingRow.getValues().get("id");
            if (existingId instanceof Integer && (Integer) existingId == id) {
                return true;
            }
        }
        return false;
    }

    private void validateRow(Row row, Table table) {
        Map<String, Object> values = row.getValues();

        for (Column column : table.getColumns()) {
            String colName = column.getName();

            if (!values.containsKey(colName))
                throw new IllegalArgumentException("Відсутнє значення для колонки: " + colName);

            Object value = values.get(colName);
            validateType(value, column);
        }
    }

    private void validateType(Object value, Column column) {
        DataType type = column.getType();

        switch (type) {
            case INTEGER -> {
                if (!(value instanceof Integer))
                    throw new IllegalArgumentException("Тип має бути INTEGER для " + column.getName());
            }
            case REAL -> {
                if (!(value instanceof Double || value instanceof Float))
                    throw new IllegalArgumentException("Тип має бути REAL для " + column.getName());
            }
            case CHAR -> {
                if (!(value instanceof Character))
                    throw new IllegalArgumentException("Тип має бути CHAR для " + column.getName());
            }
            case STRING -> {
                if (!(value instanceof String))
                    throw new IllegalArgumentException("Тип має бути STRING для " + column.getName());
            }
            case EMAIL -> {
                if (!(value instanceof String) || !((String) value).matches("^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$"))
                    throw new IllegalArgumentException("Невірний формат EMAIL для " + column.getName());
            }
            case ENUM -> {
                if (!(value instanceof String))
                    throw new IllegalArgumentException("ENUM повинен бути рядком для " + column.getName());

                List<String> allowed = column.getEnumValues();
                if (allowed == null || !allowed.contains(value))
                    throw new IllegalArgumentException("Недопустиме значення для ENUM '" + column.getName() + "': " + value);
            }
        }
    }

    private void saveDatabase(String dbName) {
        Database db = databaseRepository.findByName(dbName).get();
        databaseRepository.save(db);
        storageService.saveToDisk(databaseRepository.getAll());
    }
}