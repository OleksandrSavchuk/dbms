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
public class Column implements Serializable {
    private String name;
    private DataType type;

    private List<String> enumValues;

    public Column(String name, DataType type) {
        this.name = name;
        this.type = type;
        this.enumValues = new ArrayList<>();
    }

}
