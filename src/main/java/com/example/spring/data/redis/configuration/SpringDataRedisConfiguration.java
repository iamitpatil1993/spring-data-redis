/**
 *
 */
package com.example.spring.data.redis.configuration;

import com.example.spring.data.redis.dto.Address;
import com.example.spring.data.redis.dto.Employee;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.DecoratingStringHashMapper;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.OxmSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configures spring data redis
 *
 * @author amit
 *
 */

@Configuration
@EnableTransactionManagement // Enable spring generic Declarative transaction management using aspects.
@PropertySource(value = { "classpath:redis.properties" })
public class SpringDataRedisConfiguration {

	@Value(value = "${redis.con.host}")
	public String host;

	@Value(value = "${redis.con.port}")
	public Integer port;

	@Value(value = "${redis.con.database}")
	public Integer database;

	/**
	 * This spring RedisConnectionFactory actually does the job of translation of connector specific
	 * exception to SpringDataException.
	 * So, spring data redis also supports consistent exception hierarchy.
	 * @return
	 */
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		/** RedisStandaloneConfiguration is used for Single node redis connection configuration.
		 *  For other type connection configuration use RedisClusterConfiguration, RedisSentinelConfiguration etc classes
		 *  as per type of redis setup needed.
		 */
		RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration(host, port);
		redisConfiguration.setDatabase(database);
		LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfiguration);
		// By default, Lettuce shares same native connection for all LettuceConnection instances. So, it internally
		// uses only one redis native connection to perform all operations, we can disable this using below flag,
		// every operation on LettuceConnection will open and close a socket.
		// lettuceConnectionFactory.setShareNativeConnection(false);
		return lettuceConnectionFactory;
	}

	/**
	 * Default key and value serializers are StringRedisSerializer, so no need to
	 * configure serializers for key and value.
	 *
	 * It used sub type of RedisConnection, StringRedisConnection which provides stringified
	 * operations instead of byte[] which is a case with RedisConnection.
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

		/*
		 Even though we use @EnableTransactionManagement, we need to manually enable transaction management per
		 RedisTemplate we use in application, by calling below method on them
		*/
		redisTemplate.setEnableTransactionSupport(true);
		return redisTemplate;
	}

	@Bean
	@Qualifier("addressRedisTemplate")
	public RedisOperations<String, Address> addressRedisTemplateWithJsonSerializer(final RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Address> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(RedisSerializer.string()); // Set key serializer to String
		redisTemplate.setHashKeySerializer(RedisSerializer.string()); // Set hash key serializer to String
		redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Address.class)); // Set hash Value serializer
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Address.class)); // set Value serializer

		/*
		 This will not only enable transaction, but also participate in existing transaction at ThreadLocal
		 created by other RedisTemplates.
		*/
		redisTemplate.setEnableTransactionSupport(true);
		return redisTemplate;
	}

	/**
	 * Good thing about XML serializer is, it does not need generic type argumet as needed for Jackson.
	 * So, we can use this serializer for all types in a same way we can use JDJSerializer.
	 *
	 * @param redisConnectionFactory
	 * @return
	 */
	@Bean
	@Qualifier("oxmSerializer")
	public RedisOperations<String, ?> redisTemplateWithOXMSerializer(final RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, ?> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);

		// String key serializers
		redisTemplate.setKeySerializer(RedisSerializer.string());
		redisTemplate.setHashKeySerializer(RedisSerializer.string());

		// OXM Serializer for Values
		XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
		xStreamMarshaller.afterPropertiesSet();

		redisTemplate.setHashValueSerializer(new OxmSerializer(xStreamMarshaller, xStreamMarshaller));
		redisTemplate.setValueSerializer(new OxmSerializer(xStreamMarshaller, xStreamMarshaller));

		return redisTemplate;
	}

    @Bean
    @Qualifier("jackson")
    public HashMapper<Employee, String, String> jacksonHashMapper() {
       // when used flatten: true, it does not serialize date and Calender fields, so it's issue, I have reported this
		// https://jira.spring.io/browse/DATAREDIS-1001
		// and when we use flatten: false, it serializes the date and calender fields, but fails to deserializer them,
		// and code breaks
		// So, I am using flatten:true, which will not serialie Date and Calender fields but atleast will able to
		// desreialize saved object.
       return new DecoratingStringHashMapper(new Jackson2HashMapper(true));
    }
}
