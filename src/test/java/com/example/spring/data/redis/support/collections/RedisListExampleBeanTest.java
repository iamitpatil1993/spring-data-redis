package com.example.spring.data.redis.support.collections;

import com.example.spring.data.redis.BaseTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author amit
 */
@RunWith(SpringRunner.class)
public class RedisListExampleBeanTest extends BaseTest {

    @Autowired
    private RedisListExampleBean listExampleBean;

    @Test
    public void addToList() {
        // given
        final String jobId = UUID.randomUUID().toString();

        // when
        listExampleBean.addToList(jobId);

        // then
        assertThat(listExampleBean.getPendingJobCount(), is(equalTo(1)));
        assertThat(listExampleBean.contains(jobId), is(true));
    }

    @Test
    public void getAllJobs() {
        // given
        final String jobId1 = UUID.randomUUID().toString();
        listExampleBean.addToList(jobId1);

        final String jobId2 = UUID.randomUUID().toString();
        listExampleBean.addToList(jobId2);

        // when
        List<String> allJobs = listExampleBean.getAllJobs();

        // then
        assertThat(allJobs.size(), is(equalTo(2)));
        assertThat(allJobs, hasItems(jobId1, jobId2));
    }


    @Test
    public void removeByJobId() {
        // given
        final String jobId1 = UUID.randomUUID().toString();
        listExampleBean.addToList(jobId1);

        final String jobId2 = UUID.randomUUID().toString();
        listExampleBean.addToList(jobId2);

        // when
        listExampleBean.removeByJobId(jobId1);

        // then
        assertThat(listExampleBean.getPendingJobCount(), is(greaterThanOrEqualTo(1)));
        assertThat(listExampleBean.contains(jobId1), is(false));
    }
}