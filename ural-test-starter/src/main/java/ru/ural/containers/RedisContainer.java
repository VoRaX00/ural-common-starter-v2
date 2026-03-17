package ru.ural.containers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.*;

@Slf4j
@RequiredArgsConstructor
public class RedisContainer {

    private final com.redis.testcontainers.RedisContainer redisContainer;

    public static RedisContainer of(String image) {
        var container = new com.redis.testcontainers.RedisContainer(image);
        return new RedisContainer(container);
    }

    public void configureProperties(DynamicPropertyRegistry registry) {
        if (redisContainer.isRunning()) {
            registry.add("spring.data.redis.host", redisContainer::getHost);
            registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
        }
    }

    public void start() {
        log.info("Starting Redis Container");
        redisContainer.start();
    }

    public void stop() {
        log.info("Stopping Redis Container");
        redisContainer.stop();
    }

}
