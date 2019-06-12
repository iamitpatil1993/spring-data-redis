/**
 * 
 */
package com.example.spring.data.redis;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.test.context.ContextConfiguration;

import com.example.spring.data.redis.configuration.AppConfiguration;

/**
 * @author amit
 *
 */
@ContextConfiguration(classes = { AppConfiguration.class })
public class BaseTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);

	@Autowired
	private RedisOperations<?, ?> redisTemplate;

	@Before
	@After
	public void aroundTest() {
		LOGGER.debug("flushing database ...");
		redisTemplate.execute((RedisConnection connection) -> {
			connection.flushDb(); // flushDb flushes only currently selected database and not all.
			// connection.flushAll(); flushAll flushes all keys from all databases
			LOGGER.debug("Database cleaned ...");
			return null;
		});
	}
}
