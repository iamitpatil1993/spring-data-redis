package com.example.spring.data.redis.pipeline;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * Performs batch operation without redis pipeline.
 *
 * @author amit
 */
@Component
@Primary
public class DefaultBatchOperation implements InitializingBean, BatchOperation {

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public DefaultBatchOperation(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void addToSetInBatch(String setName, int elementCount) {
        BoundSetOperations<String, String> setBoundOps = stringRedisTemplate.boundSetOps(setName);
        for (int i = 0; i < elementCount; i++) {
            setBoundOps.add(UUID.randomUUID().toString());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(stringRedisTemplate, "Null StringRedisTemplate injected!");
    }
}
