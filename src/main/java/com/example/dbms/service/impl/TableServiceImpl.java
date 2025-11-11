package com.example.dbms.service.impl;

import com.example.dbms.model.Column;
import com.example.dbms.model.DataType;
import com.example.dbms.model.Database;
import com.example.dbms.model.Table;
import com.example.dbms.repository.DatabaseRepository;
import com.example.dbms.service.StorageService;
import com.example.dbms.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TableServiceImpl implements TableService {

    private final DatabaseRepository databaseRepository;
    private final StorageService storageService;

    @Override
    public Table createTable(String dbName, String tableName, List<String> columnNames, List<String> columnTypes) {
        var dbOpt = databaseRepository.findByName(dbName);
        if (dbOpt.isEmpty()) {
            throw new IllegalArgumentException("База даних '" + dbName + "' не знайдена");
        }

        Database db = dbOpt.get();
        if (db.getTables() == null) db.setTables(new java.util.HashMap<>());
        if (db.getTables().containsKey(tableName)) {
            throw new IllegalArgumentException("Таблиця '" + tableName + "' уже існує в базі '" + dbName + "'");
        }

        if (columnNames.size() != columnTypes.size()) {
            throw new IllegalArgumentException("Кількість назв колонок і типів не збігається");
        }

        List<Column> columns = new ArrayList<>();
        for (int i = 0; i < columnNames.size(); i++) {
            try {
                DataType type = DataType.valueOf(columnTypes.get(i).toUpperCase());
                columns.add(new Column(columnNames.get(i), type));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Невідомий тип даних: " + columnTypes.get(i));
            }
        }

        Table table = new Table(tableName, columns, new ArrayList<>());
        db.getTables().put(tableName, table);
        databaseRepository.save(db);
        storageService.saveToDisk(databaseRepository.getAll());
        return table;
    }

    // Новий метод для створення таблиці з повною інформацією про колонки
    public Table createTableWithColumns(String dbName, String tableName, List<Column> columns) {
        var dbOpt = databaseRepository.findByName(dbName);
        if (dbOpt.isEmpty()) {
            throw new IllegalArgumentException("База даних '" + dbName + "' не знайдена");
        }

        Database db = dbOpt.get();
        if (db.getTables() == null) db.setTables(new java.util.HashMap<>());
        if (db.getTables().containsKey(tableName)) {
            throw new IllegalArgumentException("Таблиця '" + tableName + "' уже існує в базі '" + dbName + "'");
        }

        Table table = new Table(tableName, columns, new ArrayList<>());
        db.getTables().put(tableName, table);
        databaseRepository.save(db);
        storageService.saveToDisk(databaseRepository.getAll());
        return table;
    }

    @Override
    public void deleteTable(String dbName, String tableName) {
        var dbOpt = databaseRepository.findByName(dbName);
        if (dbOpt.isEmpty()) throw new IllegalArgumentException("База даних '" + dbName + "' не знайдена");

        Database db = dbOpt.get();
        if (db.getTables() == null || !db.getTables().containsKey(tableName)) {
            throw new IllegalArgumentException("Таблиця '" + tableName + "' не знайдена в базі '" + dbName + "'");
        }

        db.getTables().remove(tableName);
        storageService.saveToDisk(databaseRepository.getAll());
    }

    @Override
    public List<Table> listTables(String dbName) {
        var dbOpt = databaseRepository.findByName(dbName);
        if (dbOpt.isEmpty()) throw new IllegalArgumentException("База даних '" + dbName + "' не знайдена");

        Database db = dbOpt.get();
        if (db.getTables() == null) return new ArrayList<>();
        return new ArrayList<>(db.getTables().values());
    }

    @Override
    public Table getTable(String dbName, String tableName) {
        var dbOpt = databaseRepository.findByName(dbName);
        if (dbOpt.isEmpty()) throw new IllegalArgumentException("База даних '" + dbName + "' не знайдена");

        Database db = dbOpt.get();
        if (db.getTables() == null || !db.getTables().containsKey(tableName)) {
            throw new IllegalArgumentException("Таблиця '" + tableName + "' не знайдена");
        }
        return db.getTables().get(tableName);
    }
}
