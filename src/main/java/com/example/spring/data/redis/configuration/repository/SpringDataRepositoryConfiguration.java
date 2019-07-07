package com.example.spring.data.redis.configuration.repository;

import com.example.spring.data.redis.configuration.DefaultIndexConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/**
 * Configures spring data redis repository support.
 *
 * @author amit
 */
@Configuration
@EnableRedisRepositories(basePackages = {"com.example.spring.data.redis.repository"},
        indexConfiguration = DefaultIndexConfiguration.class, // declare custom IndexConfiguration class here.
        enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class SpringDataRepositoryConfiguration {


    /**
     * By default spring-data repository needs redis template instance with name of 'redisTemplate'.
     * So, we need RedisTemplate bean in application context with name/id of 'redisTemplate'.
     * We can override this default bean name expected by using 'redisTemplateRed' property of
     * EnableRedisRepositories annotation.
     *
     * @param redisConnectionFactory
     * @return RedisTemplate to be used for spring data repository implementation.
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(final RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        /*
         No need to set serializers here. Rather it does not matter which Serializer we use spring do not use this
        serializer. I tried using StringRedisTemplate, redis template with JDK based serializer but result in both case was same.
        Which means spring repository implementation do not use serializers set on RedisTemplate.
        */
        return redisTemplate;
    }
}
