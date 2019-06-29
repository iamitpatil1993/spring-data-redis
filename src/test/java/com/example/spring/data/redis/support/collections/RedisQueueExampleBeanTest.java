package com.example.spring.data.redis.support.collections;

import com.example.spring.data.redis.BaseTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
public class RedisQueueExampleBeanTest extends BaseTest {

    @Autowired
    private RedisQueueExampleBean queueExampleBean;

    @Test
    public void addToQueue() {
        // given
        final String jobId = UUID.randomUUID().toString();

        // when
        queueExampleBean.addToQueue(jobId);

        // then
        assertThat(queueExampleBean.getElement(), is(notNullValue()));
        assertThat(queueExampleBean.peek(), is(equalTo(jobId)));
    }


    /**
     * Redis queue follows Java queue convention. Since java queue return null if peek() is called
     * on empty queue, RedisList implementation also follows this convention, hence provides complete abstraction over
     * redis via Queue interface, which can be easily replaced with any java.util Queue implementation.
     */
    @Test
    public void peekWithEmptyQueue() {
        // given
        // empty queue

        // when
        String jobId = queueExampleBean.peek();

        // then
        // peek will not throw exception, rather returns null value.
        assertThat(jobId, is(nullValue()));
    }

    @Test
    public void remove() {
        // given
        final String jobId = UUID.randomUUID().toString();
        queueExampleBean.addToQueue(jobId);

        // when
        String removedJobId = queueExampleBean.remove();

        // then
        assertThat(removedJobId, is(equalTo(jobId)));
        assertThat(queueExampleBean.peek(), is(nullValue()));
    }

    @Test(expected = NoSuchElementException.class)
    public void removeWithEmptyQueue() {
        // given
        // Empty queue

        // when
        queueExampleBean.remove();
    }

    @Test
    public void poll() {
        // given
        final String jobId = UUID.randomUUID().toString();
        queueExampleBean.addToQueue(jobId);

        // when
        String removedJobId = queueExampleBean.poll();

        // then
        assertThat(removedJobId, is(equalTo(jobId)));
        assertThat(queueExampleBean.peek(), is(nullValue()));
    }

    @Test
    public void pollWithEmptyQueue() {
        // given
        // Empty queue

        // when
        String removedJobId = queueExampleBean.poll();

        // then
        assertThat(removedJobId, is(nullValue()));
    }

    /**
     * Redis queue follows Java queue convention. Since java queue throws NoSuchElementException if element() is called
     * on empty queue, RedisList implementation also follows this convention, hence provides complete abstraction over
     * redis via Queue interface, which can be easily replaced with any java.util Queue implementation.
     */
    @Test(expected = NoSuchElementException.class)
    public void getElementWithEmptyQueue() {
        // given
        // empty queue

        // when
        String jobId = queueExampleBean.getElement();
    }
}