package com.example.dbms.controller;

import com.example.dbms.model.Column;
import com.example.dbms.service.ColumnService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/databases/{dbName}/tables/{tableName}/columns")
@RequiredArgsConstructor
public class ColumnController {

    private final ColumnService columnService;

    @GetMapping
    public List<Column> listColumns(@PathVariable String dbName, @PathVariable String tableName) {
        return columnService.listColumns(dbName, tableName);
    }

    @PostMapping
    public Column addColumn(@PathVariable String dbName,
                            @PathVariable String tableName,
                            @RequestBody Column column) {
        return columnService.addColumn(dbName, tableName, column);
    }

    @PutMapping("/{columnName}")
    public Column updateColumn(@PathVariable String dbName,
                               @PathVariable String tableName,
                               @PathVariable String columnName,
                               @RequestBody Column column) {
        return columnService.updateColumn(dbName, tableName, columnName, column);
    }

    @DeleteMapping("/{columnName}")
    public void deleteColumn(@PathVariable String dbName,
                             @PathVariable String tableName,
                             @PathVariable String columnName) {
        columnService.deleteColumn(dbName, tableName, columnName);
    }
}
