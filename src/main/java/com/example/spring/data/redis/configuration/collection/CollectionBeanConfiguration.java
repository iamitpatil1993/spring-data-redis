package com.example.spring.data.redis.configuration.collection;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.support.collections.DefaultRedisList;
import org.springframework.data.redis.support.collections.RedisList;

/**
 * Configures RedisCollections as a beans to be used in application.
 *
 * @author amit
 */
@Configuration
public class CollectionBeanConfiguration {

    @Bean
    @Qualifier("jobList")
    public RedisList redisList(RedisOperations<String, String> redisOperations) {
        return new DefaultRedisList("jobList", redisOperations);
    }

}
