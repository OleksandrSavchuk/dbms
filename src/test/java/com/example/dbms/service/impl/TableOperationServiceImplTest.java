package com.example.dbms.service.impl;

import com.example.dbms.model.*;
import com.example.dbms.repository.DatabaseRepository;
import com.example.dbms.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("TableOperationService Unit Tests")
class TableOperationServiceImplTest {

    @Mock
    private DatabaseRepository databaseRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private TableOperationServiceImpl tableOperationService;

    private Database database;
    private Table table1;
    private Table table2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Створюємо колонки
        Column idCol = new Column("id", DataType.INTEGER);
        Column nameCol = new Column("name", DataType.STRING);
        List<Column> columns = List.of(idCol, nameCol);

        // Створюємо рядки для таблиць
        Row row1 = new Row(Map.of("id", 1, "name", "Alice"));
        Row row2 = new Row(Map.of("id", 2, "name", "Bob"));
        Row row3 = new Row(Map.of("id", 1, "name", "Charlie"));

        table1 = new Table();
        table1.setName("table1");
        table1.setColumns(columns);
        table1.setRows(new ArrayList<>(List.of(row1, row2)));

        table2 = new Table();
        table2.setName("table2");
        table2.setColumns(columns);
        table2.setRows(new ArrayList<>(List.of(row2, row3)));

        database = new Database("testDB", new HashMap<>());
        database.getTables().put("table1", table1);
        database.getTables().put("table2", table2);
    }

    @Test
    @DisplayName("Тест 1: Успішне об'єднання двох таблиць (unionTables)")
    void testUnionTables_Success() {
        // Arrange
        when(databaseRepository.findByName("testDB")).thenReturn(Optional.of(database));
        when(databaseRepository.getAll()).thenReturn(Map.of("testDB", database));

        // Act
        Table result = tableOperationService.unionTables("testDB", "table1", "table2", "merged_table");

        // Assert
        assertNotNull(result, "Результат не повинен бути null");
        assertEquals("merged_table", result.getName(), "Назва результуючої таблиці має співпадати");
        assertEquals(3, result.getRows().size(), "Має бути 3 унікальні рядки (Alice, Bob, Charlie)");

        // Перевіряємо наявність колонок
        assertEquals(List.of("id", "name"),
                result.getColumns().stream().map(Column::getName).toList(),
                "Колонки мають бути однакові з початковими");

        // Перевіряємо, що ID перегенеровані
        Set<Integer> ids = result.getRows().stream()
                .map(row -> (Integer) row.getValues().get("id"))
                .collect(Collectors.toSet());
        assertEquals(Set.of(1, 2, 3), ids, "Нові ID мають бути 1, 2, 3");

        verify(databaseRepository, times(1)).save(database);
        verify(storageService, times(1)).saveToDisk(any());
    }

    @Test
    @DisplayName("Тест 2: Помилка — база даних не знайдена")
    void testUnionTables_DatabaseNotFound() {
        // Arrange
        when(databaseRepository.findByName("missingDB")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tableOperationService.unionTables("missingDB", "table1", "table2", "merged_table"),
                "Має бути помилка при відсутності БД"
        );

        assertTrue(exception.getMessage().contains("Database not found"));
        verify(databaseRepository, never()).save(any());
        verify(storageService, never()).saveToDisk(any());
    }

    @Test
    @DisplayName("Тест 3: Помилка — одна з таблиць не знайдена")
    void testUnionTables_TableNotFound() {
        // Arrange
        database.getTables().remove("table2");
        when(databaseRepository.findByName("testDB")).thenReturn(Optional.of(database));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tableOperationService.unionTables("testDB", "table1", "missing_table", "merged_table"),
                "Має бути помилка при відсутності таблиці"
        );

        assertTrue(exception.getMessage().contains("Table not found"));
        verify(databaseRepository, never()).save(any());
        verify(storageService, never()).saveToDisk(any());
    }

    @Test
    @DisplayName("Тест 4: Помилка — таблиці мають різні колонки")
    void testUnionTables_ColumnMismatch() {
        // Arrange
        Column idCol = new Column("id", DataType.INTEGER);
        Column ageCol = new Column("age", DataType.INTEGER); // інша колонка

        Table otherTable = new Table();
        otherTable.setName("table2");
        otherTable.setColumns(List.of(idCol, ageCol)); // не збігаються
        otherTable.setRows(List.of(new Row(Map.of("id", 1, "age", 20))));

        database.getTables().put("table2", otherTable);
        when(databaseRepository.findByName("testDB")).thenReturn(Optional.of(database));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tableOperationService.unionTables("testDB", "table1", "table2", "merged_table"),
                "Має бути помилка при різних колонках"
        );

        assertTrue(exception.getMessage().contains("Tables do not match"));
        verify(databaseRepository, never()).save(any());
        verify(storageService, never()).saveToDisk(any());
    }

    @Test
    @DisplayName("Тест 5: Перевірка, що дублікати рядків не додаються повторно")
    void testUnionTables_NoDuplicateRows() {
        // Arrange
        table2.setRows(List.of(
                new Row(Map.of("id", 10, "name", "Alice")), // дубль
                new Row(Map.of("id", 11, "name", "Bob"))    // дубль
        ));

        when(databaseRepository.findByName("testDB")).thenReturn(Optional.of(database));
        when(databaseRepository.getAll()).thenReturn(Map.of("testDB", database));

        // Act
        Table result = tableOperationService.unionTables("testDB", "table1", "table2", "merged_table");

        // Assert
        assertEquals(2, result.getRows().size(), "Має бути лише 2 унікальні рядки (Alice, Bob)");
        verify(databaseRepository, times(1)).save(any());
        verify(storageService, times(1)).saveToDisk(any());
    }
}
