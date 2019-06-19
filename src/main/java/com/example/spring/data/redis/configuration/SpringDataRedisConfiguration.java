/**
 * 
 */
package com.example.spring.data.redis.configuration;

import com.example.spring.data.redis.dto.Employee;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

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
	@Primary
	@Qualifier("stringTemplate")
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		return new StringRedisTemplate(redisConnectionFactory);
	}

	/**
	 * Jdk serialization based generic RedisTemplate. It uses Jdk serializer in spring to serialize
	 * value.
	 * We are able to reuse same redis template because, JdkSerializer do not need type information.
	 * So, we can reuse this same tempalte in all places.
	 *
	 */
	@Bean
	@Qualifier("jdkserialization")
	public RedisOperations<String, ?> redisTemplateWithJdkSerializer(final RedisConnectionFactory redisConnectionFactory) {
		// Default serializer is JdkSerializationRedisSerializer so no need to set value serializer
		// just set key and hash key serializers to string as we want keys to be serialized as as string
		RedisTemplate<String, ?> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(RedisSerializer.string()); // Using String serialization for key
		redisTemplate.setHashKeySerializer(RedisSerializer.string()); // Hash data structure keys will be
		// serialized using string serializer.
		return redisTemplate;
	}

	/**
	 * Jackson2JsonRedisSerializer requires Target type information and that information is part of state of
	 * Jackson2JsonRedisSerializer. So, we can not reuse this object for types other than Employee, since Employee
	 * type information is part of state of Jackson2JsonRedisSerializer.
	 * So, we need to create sepate instance for each different type, which are can do at Repository level or
	 * here at configuration level.
	 *
	 * NOTE: We can switch between serializers using configuration without any code changes.
	 */
	@Bean
	@Qualifier("employeeRedisTemplate")
	public RedisOperations<String, Employee> employeeRedisTemplateWithJsonSerializer(final RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Employee> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(RedisSerializer.string()); // Set key serializer to String
		redisTemplate.setHashKeySerializer(RedisSerializer.string()); // Set hash key serializer to String
		redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Employee.class)); // Set hash Value serializer
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Employee.class)); // set Value serializer

		return redisTemplate;
	}
}
