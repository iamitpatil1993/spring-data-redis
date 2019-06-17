package com.example.spring.data.redis.type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This bean demonstrate Hash data structure and operations.
 *
 * @author amit
 */
@Component
public class HashTypeOperation<V> implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(HashTypeOperation.class);
    private RedisOperations<String, V> redisOperations;
    private HashOperations<String, String, V> hashOperations;

    @Autowired
    public HashTypeOperation(RedisOperations<String, V> redisOperations) {
        this.redisOperations = redisOperations;
    }

    public void put(final String key, final String hashKey, V hashValue) {
        hashOperations.put(key, hashKey, hashValue);
    }

    public Optional<V> get(final String key, final String hashKey) {
        return Optional.ofNullable(hashOperations.get(key, hashKey));
    }

    public Optional<Long> size(final String key) {
        return Optional.ofNullable(hashOperations.size(key));
    }

    public void putMultiple(final String key, Map<String, V> values) {
        hashOperations.putAll(key, values);
    }

    public boolean remove(final String key, final String hashKey) {
        return hashOperations.delete(key, hashKey) > 0;
    }

    public Optional<Map<String, V>> getAll(final String key) {
        return Optional.ofNullable(hashOperations.entries(key));
    }

    /**
     * Scan and all it's variants specific to data structure (for example HSCAN for Hash) are used to incrementally
     * iterate over
     * date in data structure.
     * The downside of commands like KEYS or SMEMBERS that may block the server for a long time (even several seconds)
     * when called against big collections of keys or elements.
     * In such a cases, we should consider using SCAN to iterate over data. It fetches small chunks of data per
     * iteration.
     * <br/>
     * <a href="https://redis.io/commands/scan">Refer this for Redis SCAN</a>
     */
    public Optional<Map<String, V>> getAllUsingScan(final String key) throws IOException {
        Map<String, V> hash = new HashMap<>();
        try (Cursor<Map.Entry<String, V>> cursor = hashOperations.scan(key, ScanOptions.scanOptions().build())) {
            while (cursor.hasNext()) {
                Map.Entry<String, V> entry = cursor.next();
                hash.put(entry.getKey(), entry.getValue());
            }
        }
        return Optional.of(hash);
    }

    @Override
    public void afterPropertiesSet() {
        assert redisOperations != null : "Redis operations injected null ...";
        hashOperations = redisOperations.opsForHash();
    }
}
