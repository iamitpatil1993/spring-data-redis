package com.example.spring.data.redis.support.collections;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

/**
 * This class has no idea about, what is implementation of this java.util.List. This class has
 * no idea about this list is backed by redis and not in memory collection.
 * <p>
 * Hence, this class becomes decoupled from List implementation, so we can inject any other implementation as well.
 * For example, for testing we can inject normal ArrayList implementation and this class will function without single change.
 * </p>
 * <p>
 * NOTE: Class remains decoupled as long as we delegate instantiation of List to configuration. But there is one
 * problem here, in order to decouple this class from implementation we will declare List implementation as a bean in
 * configuration and inject here, but how many redis list we will declare as a bean in config class?
 * </p>
 */
@Component
public class RedisListExampleBean implements InitializingBean {

    private List<String> jobList;

    @Autowired
    public RedisListExampleBean(@Qualifier("jobList") List<String> jobList) {
        this.jobList = jobList;
    }

    public void addToList(final String jobId) {
        jobList.add(jobId);
    }

    public List<String> getAllJobs() {
        return jobList;
    }

    public Integer getPendingJobCount() {
        return jobList.size();
    }

    public boolean removeByJobId(final String jobId) {
        return jobList.remove(jobId);
    }

    public boolean contains(final String jobId) {
        return jobList.contains(jobId);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(jobList, "List injected null!");
    }
}
