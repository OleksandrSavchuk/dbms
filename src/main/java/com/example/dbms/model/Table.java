package com.example.dbms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Table implements Serializable {
    private String name;
    private List<Column> columns = new ArrayList<>();
    private List<Row> rows = new ArrayList<>();
    private int nextId;

    public Table(String name, List<Column> columns, List<Row> rows) {
        this.name = name;
        this.columns = columns;
        this.rows = rows;
        this.nextId = 1;
    }

}
