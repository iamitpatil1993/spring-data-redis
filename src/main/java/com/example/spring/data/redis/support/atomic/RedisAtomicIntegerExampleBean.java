package com.example.spring.data.redis.support.atomic;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Similar to java atomic classees in java.util.concurrent.atomic spring data redis provides classes for atomic
 * operations on Integer, Double and Long data types.
 *
 * <p>Classes supported by spring data redis are mentioned
 * <a href="https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/support/atomic/package-frame.html">here</a></p>
 * <p>
 * <p><b>NOTE:</b> Only issue with these calsses is, they do not provide implementation via java interfaces as redis collection classes does,
 * so, we need directly use implementation classes wherever required instead of interfaces and DI. </p>
 * <p>
 * <p><Similar to RedisAtomicInteger, we can use Long and Double classes./p>
 *
 * @author amit
 */
@Component
public class RedisAtomicIntegerExampleBean implements InitializingBean {

    // Need to use class directly.
    private RedisAtomicInteger redisAtomicInteger;
    private RedisConnectionFactory connectionFactory;

    @Autowired
    public RedisAtomicIntegerExampleBean(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public int get() {
        return redisAtomicInteger.get();
    }

    public void set(final int newValue) {
        redisAtomicInteger.set(newValue);
    }

    public int increamentAndGet() {
        return redisAtomicInteger.incrementAndGet();
    }

    public String getAssociatedKey() {
        return redisAtomicInteger.getKey();
    }

    public int getAndAdd(final int delta) {
        return redisAtomicInteger.getAndAdd(delta);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(connectionFactory, "RedisConnectionFactory injected null!");
        this.redisAtomicInteger = new RedisAtomicInteger("testCounter", connectionFactory);
    }
}
