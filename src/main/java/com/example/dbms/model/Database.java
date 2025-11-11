package com.example.dbms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Database implements Serializable {
    private String name;
    private Map<String, Table> tables = new HashMap<>();
}
