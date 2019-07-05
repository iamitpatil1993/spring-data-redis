package com.example.spring.data.redis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;

import java.util.UUID;

/**
 * @author amit
 */
@Getter
@AllArgsConstructor
// Intentionally no providing setters and no-arg constructor sice id is immutable.
public class BaseEntity {

    /**
     * Spring automatically uses property with name 'id' as a PK for entity.
     * still we can use @Id for readability purpose.
     */
    @Id
    private final UUID id;

}
