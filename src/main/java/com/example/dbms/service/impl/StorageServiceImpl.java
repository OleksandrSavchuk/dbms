package com.example.dbms.service.impl;

import com.example.dbms.model.Database;
import com.example.dbms.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    private static final String STORAGE_FILE = "databases.dat";

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Database> loadFromDisk() {
        File file = new File(STORAGE_FILE);
        if (!file.exists()) {
            log.info("Файл збереження відсутній. Створюється новий.");
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map<?, ?> map) {
                log.info("Бази даних успішно завантажено з диску ({}).", STORAGE_FILE);
                return (Map<String, Database>) map;
            }
        } catch (Exception e) {
            log.error("Помилка при зчитуванні баз даних: {}", e.getMessage());
        }
        return new HashMap<>();
    }

    @Override
    public void saveToDisk(Map<String, Database> databases) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORAGE_FILE))) {
            oos.writeObject(databases);
            log.info("Бази даних збережено на диск ({}).", STORAGE_FILE);
        } catch (IOException e) {
            log.error("Помилка при збереженні баз даних: {}", e.getMessage());
        }
    }

}
