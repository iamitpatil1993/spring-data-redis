/**
 * 
 */
package com.example.spring.data.redis.type;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author amit
 *
 */
@Component
public class StringTypeOperation implements InitializingBean {

	private RedisOperations<String, String> redisTemplate;
	private ValueOperations<String, String> valueOperations;

	@Autowired
	public StringTypeOperation(@Qualifier("stringTemplate")  RedisOperations<String, String> stringRedisTemplate) {
		this.redisTemplate = stringRedisTemplate;
		this.valueOperations = redisTemplate.opsForValue();
	}

	public void set(final String key, final String value) {
		valueOperations.set(key, value);
	}

	public Optional<String> get(final String key) {
		return Optional.ofNullable(valueOperations.get(key));
	}

	public boolean append(final String key, final String value) {
		return valueOperations.append(key, value) != 0;
	}

	public Long increment(final String key) {
		return valueOperations.increment(key);
	}

	public Long decrement(final String key) {
		return valueOperations.decrement(key);
	}

	public Long decrement(final String key, final long decrementBy) {
		return valueOperations.decrement(key, decrementBy);
	}

	public Optional<String> subString(final String key, final long startIndex, final long endIndex) {
		return Optional.ofNullable(valueOperations.get(key, startIndex, endIndex));
	}

	public Optional<String> getAndSet(final String key, final String value) {
		return Optional.ofNullable(valueOperations.getAndSet(key, value));
	}

	public List<String> getMultiple(final List<String> keys) {
		return valueOperations.multiGet(keys);
	}

	/**
	 * Set multiple is atomic, i.e either all keys will be set or none.
	 */
	public void setmultiple(final Map<String, String> map) {
		valueOperations.multiSet(map);
	}

	public long getLength(final String key) {
		return valueOperations.size(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		assert redisTemplate != null : "RedisTemplate autowired null!";
	}

}
