package com.example.spring.data.redis.type;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;

/**
 * @author amit
 */
@Component
public class SortedSetTypeOperation<V> implements InitializingBean {

    private RedisTemplate<String, V> redisTemplate;
    private ZSetOperations<String, V> sortedSetOperations;

    @Autowired
    public SortedSetTypeOperation(RedisTemplate<String, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Boolean add(final String key, final double score, final V value) {
        return sortedSetOperations.add(key, value, score);
    }

    public Boolean add(final String key, final TypedTuple<V> tuple) {
        Set<TypedTuple<V>> tuples = new HashSet<>(1);
        tuples.add(tuple);
        return sortedSetOperations.add(key, tuples) > 0;
    }

    public Optional<Long> size(final String key) {
        Long size = sortedSetOperations.size(key);
        return size == 0 ? Optional.empty() : Optional.of(size);
    }

    public OptionalLong countByScoreRange(final String key, double minScore, double maxScore) {
        if (!redisTemplate.hasKey(key)) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(sortedSetOperations.count(key, minScore, maxScore));
    }

    public Double incrementScore(final String key, final V member, final double incrByValue) {
        return sortedSetOperations.incrementScore(key, member, incrByValue);
    }

    public Optional<Double> getScore(final String key, V member) {
        return Optional.of(sortedSetOperations.score(key, member));
    }

    public Optional<Set<TypedTuple<V>>> range(final String key, final long fromIndex, final long toIndex) {
        Set<TypedTuple<V>> range = sortedSetOperations.rangeWithScores(key, fromIndex, toIndex);
        if (range.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(range);
    }

    public Long remove(final String key, V... members) {
        return sortedSetOperations.remove(key, members);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        assert redisTemplate != null : "Injected null redis template";
        sortedSetOperations = redisTemplate.opsForZSet();
    }
}
