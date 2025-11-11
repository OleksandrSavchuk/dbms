package com.example.dbms.service.impl;

import com.example.dbms.model.Database;
import com.example.dbms.repository.DatabaseRepository;
import com.example.dbms.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("DatabaseService Unit Tests")
class DatabaseServiceImplTest {

    @Mock
    private DatabaseRepository databaseRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private DatabaseServiceImpl databaseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Тест 1: Створення нової бази даних - успішно")
    void testCreateDatabase_Success() {
        // Arrange
        String dbName = "test_database";
        when(databaseRepository.exists(dbName)).thenReturn(false);
        when(databaseRepository.getAll()).thenReturn(new HashMap<>());

        // Act
        Database result = databaseService.createDatabase(dbName);

        // Assert
        assertNotNull(result, "Результат не повинен бути null");
        assertEquals(dbName, result.getName(), "Назва БД має співпадати");

        verify(databaseRepository, times(1)).save(any(Database.class));
        verify(storageService, times(1)).saveToDisk(any());
    }

    @Test
    @DisplayName("Тест 2: Створення бази даних - помилка при дублюванні")
    void testCreateDatabase_AlreadyExists() {
        // Arrange
        String dbName = "existing_database";
        when(databaseRepository.exists(dbName)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> databaseService.createDatabase(dbName),
                "Має викинути виняток при створенні дубліката БД"
        );

        assertTrue(exception.getMessage().contains("уже існує"),
                "Повідомлення помилки має містити 'уже існує'");

        verify(databaseRepository, never()).save(any());
        verify(storageService, never()).saveToDisk(any());
    }

    @Test
    @DisplayName("Тест 3: Видалення бази даних - успішно")
    void testDeleteDatabase_Success() {
        // Arrange
        String dbName = "database_to_delete";
        when(databaseRepository.exists(dbName)).thenReturn(true);
        when(databaseRepository.getAll()).thenReturn(new HashMap<>());

        // Act
        assertDoesNotThrow(() -> databaseService.deleteDatabase(dbName),
                "Видалення має пройти без помилок");

        // Assert
        verify(databaseRepository, times(1)).delete(dbName);
        verify(storageService, times(1)).saveToDisk(any());
    }

    @Test
    @DisplayName("Тест 4: Видалення неіснуючої бази даних - помилка")
    void testDeleteDatabase_NotFound() {
        // Arrange
        String dbName = "non_existent_database";
        when(databaseRepository.exists(dbName)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> databaseService.deleteDatabase(dbName),
                "Має викинути виняток при видаленні неіснуючої БД"
        );

        assertTrue(exception.getMessage().contains("не знайдена"),
                "Повідомлення помилки має містити 'не знайдена'");

        verify(databaseRepository, never()).delete(anyString());
        verify(storageService, never()).saveToDisk(any());
    }

    @Test
    @DisplayName("Тест 5: Отримання списку баз даних")
    void testListDatabases() {
        // Arrange
        List<Database> expectedDatabases = Arrays.asList(
                new Database("db1", new HashMap<>()),
                new Database("db2", new HashMap<>()),
                new Database("db3", new HashMap<>())
        );
        when(databaseRepository.findAll()).thenReturn(expectedDatabases);

        // Act
        List<Database> result = databaseService.listDatabases();

        // Assert
        assertNotNull(result, "Результат не повинен бути null");
        assertEquals(3, result.size(), "Має повернути 3 бази даних");
        assertEquals(expectedDatabases, result, "Список БД має співпадати");

        verify(databaseRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Тест 6: Порожній список баз даних")
    void testListDatabases_Empty() {
        // Arrange
        when(databaseRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Database> result = databaseService.listDatabases();

        // Assert
        assertNotNull(result, "Результат не повинен бути null");
        assertTrue(result.isEmpty(), "Список має бути порожнім");

        verify(databaseRepository, times(1)).findAll();
    }
}