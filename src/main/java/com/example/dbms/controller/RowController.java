package com.example.dbms.controller;

import com.example.dbms.model.Row;
import com.example.dbms.service.RowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/databases/{dbName}/tables/{tableName}/rows")
@RequiredArgsConstructor
public class RowController {

    private final RowService rowService;

    @GetMapping
    public List<Row> listRows(@PathVariable String dbName, @PathVariable String tableName) {
        return rowService.listRows(dbName, tableName);
    }

    @PostMapping
    public Row addRow(@PathVariable String dbName,
                      @PathVariable String tableName,
                      @RequestBody Row row) {
        return rowService.addRow(dbName, tableName, row);
    }

    @PutMapping("/{rowIndex}")
    public Row updateRow(@PathVariable String dbName,
                         @PathVariable String tableName,
                         @PathVariable int rowIndex,
                         @RequestBody Row row) {
        return rowService.updateRow(dbName, tableName, rowIndex, row);
    }

    @DeleteMapping("/{rowIndex}")
    public void deleteRow(@PathVariable String dbName,
                          @PathVariable String tableName,
                          @PathVariable int rowIndex) {
        rowService.deleteRow(dbName, tableName, rowIndex);
    }
}
