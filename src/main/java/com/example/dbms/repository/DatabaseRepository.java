package com.example.dbms.repository;

import com.example.dbms.model.Database;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class DatabaseRepository {

    private final Map<String, Database> databases;

    public DatabaseRepository() {
        this.databases = new HashMap<>();
    }

    public DatabaseRepository(Map<String, Database> initialData) {
        this.databases = new HashMap<>(initialData);
    }

    public List<Database> findAll() {
        return new ArrayList<>(databases.values());
    }

    public Optional<Database> findByName(String name) {
        return Optional.ofNullable(databases.get(name));
    }

    public void save(Database db) {
        databases.put(db.getName(), db);
    }

    public void delete(String name) {
        databases.remove(name);
    }

    public boolean exists(String name) {
        return databases.containsKey(name);
    }

    public Map<String, Database> getAll() {
        return databases;
    }

}
