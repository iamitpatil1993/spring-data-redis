package com.example.spring.data.redis.pipeline;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * Performs batch operation using redis pipeline.
 *
 * @author amit
 */
@Component
@Qualifier("pipelined")
public class PipileBasedBatchOperationImpl implements InitializingBean, BatchOperation {

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public PipileBasedBatchOperationImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void addToSetInBatch(String setName, int elementCount) {
        stringRedisTemplate.executePipelined((RedisConnection connection) -> {
            StringRedisConnection stringRedisConnection = (StringRedisConnection) connection;
            for (int i = 0; i < elementCount; i++) {
                stringRedisConnection.sAdd(setName, UUID.randomUUID().toString());
            }
            return null;
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(stringRedisTemplate, "Null StringRedisTemplate injected!");
    }
}
