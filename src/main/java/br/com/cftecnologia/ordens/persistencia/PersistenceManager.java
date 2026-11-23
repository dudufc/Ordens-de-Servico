package br.com.cftecnologia.ordens.persistencia;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PersistenceManager {

    private static EntityManagerFactory factory;

    private PersistenceManager() {
    }

    public static synchronized EntityManagerFactory getFactory() {
        if (factory == null || !factory.isOpen()) {
            try {
                Files.createDirectories(Path.of("dados"));
            } catch (IOException exception) {
                throw new IllegalStateException("Não foi possível criar a pasta do banco de dados.", exception);
            }
            factory = Persistence.createEntityManagerFactory("cfServicePU");
        }
        return factory;
    }

    public static synchronized void fechar() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}

