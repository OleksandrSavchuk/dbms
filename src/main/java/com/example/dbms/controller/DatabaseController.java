package com.example.dbms.controller;

import com.example.dbms.model.Database;
import com.example.dbms.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/databases")
@RequiredArgsConstructor
public class DatabaseController {

    private final DatabaseService databaseService;

    @PostMapping("/{name}")
    public ResponseEntity<Database> create(@PathVariable String name) {
        return ResponseEntity.ok(databaseService.createDatabase(name));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> delete(@PathVariable String name) {
        databaseService.deleteDatabase(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Database>> list() {
        return ResponseEntity.ok(databaseService.listDatabases());
    }
}
