package com.example.dbms.controller;

import com.example.dbms.model.Table;
import com.example.dbms.service.TableOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/databases/{dbName}/tables/operations")
@RequiredArgsConstructor
public class TableOperationController {

    private final TableOperationService tableOperationService;

    @PostMapping("/union")
    public Table unionTables(@PathVariable String dbName,
                             @RequestParam String table1,
                             @RequestParam String table2,
                             @RequestParam String resultTableName) {
        return tableOperationService.unionTables(dbName, table1, table2, resultTableName);
    }
}
