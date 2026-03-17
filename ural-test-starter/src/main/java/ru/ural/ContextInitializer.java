package ru.ural;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.*;
import ru.ural.containers.PostgreSqlContainer;
import ru.ural.containers.RedisContainer;

@Slf4j
public class ContextInitializer {

    private static final PostgreSqlContainer POSTGRE_SQL_CONTAINER = PostgreSqlContainer
        .of("postgres:latest");

    private static final RedisContainer REDIS_CONTAINER = RedisContainer.of("redis:latest");

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        POSTGRE_SQL_CONTAINER.configureProperties(registry);
        REDIS_CONTAINER.configureProperties(registry);
    }

    @BeforeAll
    static void beforeAll() {
        POSTGRE_SQL_CONTAINER.start();
        REDIS_CONTAINER.start();
    }

}
