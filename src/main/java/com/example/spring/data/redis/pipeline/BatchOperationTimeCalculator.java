package com.example.spring.data.redis.pipeline;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.Map;

/**
 * Aspect that executes around BatchOperation, to calculate and expose time taken by different implementations.
 * Uses ThreadLocal to expose metrics to other part of code.
 *
 * @author amit
 */
@Component
@Aspect
public class BatchOperationTimeCalculator {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchOperationTimeCalculator.class);

    /**
     * Maintains metrics information at ThreadLocal, which can be get in other part of code
     * running in this [same] thread
     */
    private static final ThreadLocal<Map<String, Long>> timerMetricsThreadLocal = new ThreadLocal<>();

    @Around(value = "execution(void com.example.spring.data.redis.pipeline.BatchOperation.addToSetInBatch(..))")
    public void arountBatchOperation(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        BatchOperation batchOperationTarget = (BatchOperation) proceedingJoinPoint.getTarget();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // execute batch operation
        proceedingJoinPoint.proceed();

        stopWatch.stop();
        LOGGER.info("Time for BatchOperation by :: {}, is :: {}", batchOperationTarget.getClass().getName(),
                stopWatch.getLastTaskTimeMillis());

        // Save metric to ThreadLocal
        exposeMetricsToThreadLocal(batchOperationTarget, stopWatch);
    }

    /**
     * Saves metrics at thread local, to be used later by other part of code running in same thread.
     */
    private void exposeMetricsToThreadLocal(BatchOperation batchOperationTarget, StopWatch stopWatch) {
        if (timerMetricsThreadLocal.get() == null) {
            timerMetricsThreadLocal.set(new HashMap<>());
        }
        timerMetricsThreadLocal.get().put(batchOperationTarget.getClass().getName(), stopWatch.getLastTaskTimeMillis());
    }

    /**
     * Provides metrics to other part of code via getter.
     */
    public Map<String, Long> getMetrics() {
        return timerMetricsThreadLocal.get();
    }
}
