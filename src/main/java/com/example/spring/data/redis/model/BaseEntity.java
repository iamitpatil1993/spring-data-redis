package com.example.spring.data.redis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * @author amit
 */
@Getter
@AllArgsConstructor
// Intentionally no providing setters and no-arg constructor sice id is immutable.
public class BaseEntity {

    private final UUID id;

}
