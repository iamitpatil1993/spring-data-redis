/**
 * 
 */
package com.example.spring.data.redis.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Configures spring data redis
 * 
 * @author amit
 *
 */

@Configuration
@PropertySource(value = { "classpath:redis.properties" })
public class SpringDataRedisConfiguration {

	@Value(value = "${redis.con.host}")
	public String host;

	@Value(value = "${redis.con.port}")
	public Integer port;

	@Value(value = "${redis.con.database}")
	public Integer database;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisConfiguration redisConfiguration = new RedisStandaloneConfiguration(host, port);

		LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfiguration);
		lettuceConnectionFactory.setDatabase(database);

		return lettuceConnectionFactory;
	}

	/**
	 * Default key and value serializers are StringRedisSerializer, so no need to
	 * configure serializers for key and value
	 * 
	 * @param redisConnectionFactory
	 * @return StringRedisTemplate which is used for spring related Redis operations
	 *         (where value and key types are String)
	 */
	@Bean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		return new StringRedisTemplate(redisConnectionFactory);
	}
}
