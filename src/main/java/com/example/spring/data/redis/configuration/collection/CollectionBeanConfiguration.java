package com.example.spring.data.redis.configuration.collection;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.support.collections.DefaultRedisList;
import org.springframework.data.redis.support.collections.RedisList;

/**
 * Configures RedisCollections as a beans to be used in application.
 * <p>
 * Similar to RedisList, spring provides wrappers/abstraction for Set, SortedSet, Hash and Map data structures
 * which implements one or more corresponding java.util collection interfaces.
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

    /**
     * RedisList implements BlockingDeque<E>, BlockingQueue<E>, Collection<E>, Deque<E>, Iterable<E>, List<E>, Queue<E>
     * java interfaces, so RedisList can be used as a implementation for an of there or all of these data
     * strucutre contracts.
     *
     * @param redisOperations
     * @return
     */
    @Bean
    @Qualifier("jobQueue")
    public RedisList redisJobQueue(RedisOperations<String, String> redisOperations) {
        return new DefaultRedisList("jobQueue", redisOperations);
    }
}
