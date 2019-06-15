package com.example.spring.data.redis.type;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Component;

/**
 * This bean demonstrate List data structure and operations.
 * 
 * @author amit
 * @param <V> Value Type
 *
 */
@Component
public class ListTypeOperation<V> implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(ListTypeOperation.class);
	private RedisOperations<String, V> redisOperations;
	private ListOperations<String, V> listOperations;

	@Autowired
	public ListTypeOperation(RedisOperations<String, V> redisOperations) {
		this.redisOperations = (RedisOperations<String, V>) redisOperations;
	}

	public Boolean leftPush(final String listKey, final V value) {
		return listOperations.leftPush(listKey, value) > 0;
	}

	public Boolean rightPush(String listKey, V value) {
		return listOperations.rightPush(listKey, value) > 0;
	}

	/**
	 * This righPush operation, adds new element after provided pivot searching from
	 * left to right. Adds new value after pivot searching from left to right.
	 */
	public Boolean rightPushBeforeValue(String listKey, V value, V pivot) {
		return listOperations.rightPush(listKey, pivot, value) > 0;
	}

	public Optional<V> getAtIndex(final String listKey, final long index) {
		return Optional.ofNullable(listOperations.index(listKey, index));
	}

	public Optional<List<V>> getAll(final String key) {
		return Optional.ofNullable(listOperations.range(key, 0, -1));
	}

	public Optional<V> rightPop(final String key) {
		return Optional.ofNullable(listOperations.rightPop(key));
	}

	public Optional<Long> size(final String key) {
		Long size = listOperations.size(key);
		return size == 0 ? Optional.empty() : Optional.of(size);
	}

	/**
	 * Trim operation is very useful for creating FIX length queues/lists. For
	 * example query/list with latest N posts. See redis docs for more details
	 * 
	 * @see <a href="https://redis.io/topics/data-types-intro">Redis Documentation:
	 *      Capped List</a>
	 * @param key
	 * @return
	 */
	public void trim(final String key, final long startIndex, final long endIndex) {
		listOperations.trim(key, startIndex, endIndex);
	}

	/**
	 * There is no way in redis list to delete element at index. We can delete
	 * element by it's value using LREM. So, this delete by index is a hack, where
	 * we first set the value of list item to be deleted to new value (something
	 * like UUID/ TO_BE_DELETED) and then delete that element by value using LREM.
	 * Better setting temporary value to UUID
	 * This is two step process so we need to do this in redis transaction.
	 * 
	 * @param key
	 * @param index
	 */
	public void deleteAtIndex(final String key, final long index) {
		String listItemToDelete = UUID.randomUUID().toString();
		/*
		 * This is not working and throwing exception, will check how to handle
		 * transactions in better way in latter examples 
			  redisOperations.multi(); 
			  //code 
			  redisOperations.exec();
		 */
		redisOperations.execute(new RedisCallback<Void>() {
			@Override
			public Void doInRedis(RedisConnection redisConnection) throws DataAccessException {
				redisConnection.multi(); // start transaction
				redisConnection.lSet(key.getBytes(), index, listItemToDelete.getBytes()); // set value if listItem to be
				// deleted to UUID
				redisConnection.lRem(key.getBytes(), 1, listItemToDelete.getBytes()); // delete by value
				redisConnection.exec(); // execute/commit transaction
				return null;
			}
		});
	}

	/**
	 * This method will act as a consumer to consume list (which can be used as a queue).
	 * It will continually pull data from redis queue and will process it.
	 * <p>
	 * But this way of consumer is not reliable, because what if this consumer pops data from
	 * queue and not able to process it or consumer itself gets shutdown down or killed.
	 * Recond in queue will remain unprocessed, so better way of queuing and consumer is
	 * mentioned here in redis docs where we should consider usin intermediate list using BRPOPLPUSH.
	 * <br/>
	 * <a href="https://redis.io/commands/rpoplpush">Reliable queue using redis</a>
	 *
	 * @param key
	 */
	public void leftPopBlocked(final String key) {
		while (true) {
			V value = listOperations.leftPop(key, 5l, TimeUnit.SECONDS);
			if (value != null) {
				LOGGER.info("red list Item :: {}", value);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		assert redisOperations != null : "Null redisOperations injected ...";
		this.listOperations = redisOperations.opsForList();
	}
}
