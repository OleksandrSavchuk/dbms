package com.example.dbms.service;

import com.example.dbms.model.Database;

import java.util.Map;

public interface StorageService {

    Map<String, Database> loadFromDisk();

    void saveToDisk(Map<String, Database> databases);

}
