package com.example.dbms.service;

import com.example.dbms.model.Database;

import java.util.List;

public interface DatabaseService {

    Database createDatabase(String name);

    void deleteDatabase(String name);

    List<Database> listDatabases();

}
