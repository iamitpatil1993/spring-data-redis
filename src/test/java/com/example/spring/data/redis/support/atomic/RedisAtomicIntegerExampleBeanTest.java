package com.example.spring.data.redis.support.atomic;

import com.example.spring.data.redis.BaseTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

/**
 * @author amit
 */
@RunWith(SpringRunner.class)
public class RedisAtomicIntegerExampleBeanTest extends BaseTest {

    @Autowired
    private RedisAtomicIntegerExampleBean redisAtomicIntegerExampleBean;

    /**
     * Since RedisAtomicInteger do not abstract java atomic classes, instead of returning zero, it throws exception
     * if no key exists in redis.
     */
    @Test(expected = DataRetrievalFailureException.class)
    public void testGet() {
        // given
        // No current value

        // when
        redisAtomicIntegerExampleBean.get();
    }

    @Test
    public void testGetWithValue() {
        // given
        redisAtomicIntegerExampleBean.set(100);

        // when
        int currentValue = redisAtomicIntegerExampleBean.get();

        // then
        assertEquals(100, currentValue);
    }

    @Test
    public void testGncreamentAndGet() {
        // given
        redisAtomicIntegerExampleBean.set(100);

        // when
        int increamentedValue = redisAtomicIntegerExampleBean.increamentAndGet();

        // then
        assertEquals(101, increamentedValue);
    }

    @Test
    public void testGetAssociatedKey() {
        // when
        String atomicIntegerKeyName = redisAtomicIntegerExampleBean.getAssociatedKey();

        // then
        assertEquals("testCounter", atomicIntegerKeyName);
    }

    @Test
    public void getAndAdd() {
        // given
        final int initialiValue = 100;
        redisAtomicIntegerExampleBean.set(initialiValue);

        // when
        int valueBeforeAddingDelta = redisAtomicIntegerExampleBean.getAndAdd(50);

        // then
        assertEquals(initialiValue, valueBeforeAddingDelta);
        int valueAfterAdition = redisAtomicIntegerExampleBean.get();
        assertEquals(150, valueAfterAdition);
    }
}