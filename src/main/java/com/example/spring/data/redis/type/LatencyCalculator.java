package com.example.spring.data.redis.type;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * This aspect advice HashTypeOperation.getAll*Scan to calculate time taken by scan operation.
 *
 * @author amit
 */
@Aspect
@Component
public class LatencyCalculator {
    private static final Logger LOGGER = LoggerFactory.getLogger(LatencyCalculator.class);

    @Around(value = "execution(* com.example.spring.data.redis.type.HashTypeOperation.getAll*Scan(..))")
    public void calculateTime(final ProceedingJoinPoint proceedingJoinPoint) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return;
        }
        stopWatch.stop();
        LOGGER.info("Method :: {}, Time :: {}", proceedingJoinPoint.getSignature().toShortString(),
                stopWatch.getLastTaskTimeMillis());
    }
}
