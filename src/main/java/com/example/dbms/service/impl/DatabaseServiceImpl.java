package com.example.dbms.service.impl;

import com.example.dbms.model.Database;
import com.example.dbms.repository.DatabaseRepository;
import com.example.dbms.service.DatabaseService;
import com.example.dbms.service.StorageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DatabaseServiceImpl implements DatabaseService {

    private final DatabaseRepository databaseRepository;

    private final StorageService storageService;

    @PostConstruct
    public void init() {
        Map<String, Database> loaded = storageService.loadFromDisk();
        loaded.forEach((name, db) -> databaseRepository.save(db));
    }

    @Override
    public Database createDatabase(String name) {
        if (databaseRepository.exists(name)) {
            throw new IllegalArgumentException("База даних '" + name + "' уже існує");
        }
        Database db = new Database(name, null);
        databaseRepository.save(db);
        storageService.saveToDisk(databaseRepository.getAll());
        return db;
    }

    @Override
    public void deleteDatabase(String name) {
        if (!databaseRepository.exists(name)) {
            throw new IllegalArgumentException("База даних '" + name + "' не знайдена");
        }
        databaseRepository.delete(name);
        storageService.saveToDisk(databaseRepository.getAll());
    }

    @Override
    public List<Database> listDatabases() {
        return databaseRepository.findAll();
    }

}
