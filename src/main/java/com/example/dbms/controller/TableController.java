package com.example.dbms.controller;

import com.example.dbms.model.Column;
import com.example.dbms.model.DataType;
import com.example.dbms.model.Table;
import com.example.dbms.service.impl.TableServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/databases/{dbName}/tables")
@RequiredArgsConstructor
public class TableController {

    private final TableServiceImpl tableService;

    @PostMapping
    public ResponseEntity<Table> createTable(
            @PathVariable String dbName,
            @RequestBody Map<String, Object> body
    ) {
        String tableName = (String) body.get("tableName");

        if (body.containsKey("columns")) {
            List<Map<String, Object>> columnsData = (List<Map<String, Object>>) body.get("columns");
            List<Column> columns = new ArrayList<>();

            for (Map<String, Object> colData : columnsData) {
                String name = (String) colData.get("name");
                String typeStr = (String) colData.get("type");
                DataType type = DataType.valueOf(typeStr.toUpperCase());

                Column column = new Column(name, type);

                if (colData.containsKey("enumValues")) {
                    List<String> enumValues = (List<String>) colData.get("enumValues");
                    column.setEnumValues(enumValues);
                }

                columns.add(column);
            }

            return ResponseEntity.ok(tableService.createTableWithColumns(dbName, tableName, columns));
        } else {
            List<String> columnNames = (List<String>) body.get("columnNames");
            List<String> columnTypes = (List<String>) body.get("columnTypes");
            return ResponseEntity.ok(tableService.createTable(dbName, tableName, columnNames, columnTypes));
        }
    }

    @DeleteMapping("/{tableName}")
    public ResponseEntity<Void> deleteTable(@PathVariable String dbName, @PathVariable String tableName) {
        tableService.deleteTable(dbName, tableName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Table>> listTables(@PathVariable String dbName) {
        return ResponseEntity.ok(tableService.listTables(dbName));
    }

    @GetMapping("/{tableName}")
    public ResponseEntity<Table> getTable(@PathVariable String dbName, @PathVariable String tableName) {
        return ResponseEntity.ok(tableService.getTable(dbName, tableName));
    }
}
