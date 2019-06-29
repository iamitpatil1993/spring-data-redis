package com.example.spring.data.redis.support.collections;

/**
 * This bean demonstrates use of RedisList as a java Queue abstraction backed by Redis.
 * This bean is completely decoupled, how Queue is implemented. We can inject any other Queue implementation.
 *
 * @author amit
 */

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Queue;

@Component
public class RedisQueueExampleBean implements InitializingBean {

    // RedisList as a Queue implementation
    private Queue<String> jobQueue;

    @Autowired
    public RedisQueueExampleBean(@Qualifier("jobQueue") Queue<String> jobQueue) {
        this.jobQueue = jobQueue;
    }

    public boolean addToQueue(final String jobId) {
        return jobQueue.offer(jobId);
    }

    public String getElement() {
        return jobQueue.element();
    }

    public String peek() {
        return jobQueue.peek();
    }

    public String remove() {
        return jobQueue.remove();
    }

    public String poll() {
        return jobQueue.poll();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(jobQueue, "Queue injected null!");
    }
}
