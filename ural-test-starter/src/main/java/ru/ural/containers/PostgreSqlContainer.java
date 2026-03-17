package ru.ural.containers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.*;
import org.testcontainers.containers.*;

@Slf4j
@RequiredArgsConstructor
public class PostgreSqlContainer {

    private final PostgreSQLContainer<?> container;

    public static PostgreSqlContainer of(String image) {
        var container = new PostgreSQLContainer<>(image);
        return new PostgreSqlContainer(container);
    }

    public void configureProperties(DynamicPropertyRegistry registry) {
        if (container.isRunning()) {
            registry.add("spring.datasource.url", container::getJdbcUrl);
            registry.add("spring.datasource.username", container::getUsername);
            registry.add("spring.datasource.password", container::getPassword);
        }
    }

    public void start() {
        try {
            log.info("Starting PostgreSQL container");
            container.start();
        } catch (Exception e) {
            log.error("Error starting PostgreSQL container", e);
        }
    }

    public void stop() {
        log.info("Stopping PostgreSQL container");
        container.stop();
    }

}
