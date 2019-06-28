package com.example.spring.data.redis.pipeline;

import com.example.spring.data.redis.BaseTest;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static org.junit.Assert.assertThat;

/**
 * @author amit
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class DefaultBatchOperationTest extends BaseTest {

    @Autowired
    private BatchOperation batchOperation;

    @Autowired
    @Qualifier("pipelined")
    private BatchOperation pipelinedBatchOperation;

    @Autowired
    private BatchOperationTimeCalculator batchOperationTimeCalculator;

    @Test
    public void addToSetInBatch() {
        // given
        final String name = "names";
        int elementCount = 10000;

        // when
        batchOperation.addToSetInBatch(name, elementCount); // Normal exec
        pipelinedBatchOperation.addToSetInBatch(name, elementCount); // pipelined exec.

        // then
        Map<String, Long> metrics = batchOperationTimeCalculator.getMetrics();

        Long defaultBatchOperationTimeMiles = metrics.get(DefaultBatchOperation.class.getName());
        Long pipelinedBatchOperationTimeMiles = metrics.get(PipileBasedBatchOperationImpl.class.getName());

        assertThat(defaultBatchOperationTimeMiles, Matchers.is(Matchers.greaterThan(pipelinedBatchOperationTimeMiles)));
    }
}