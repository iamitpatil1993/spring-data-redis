package com.example.spring.data.redis.pipeline;

public interface BatchOperation {

    void addToSetInBatch(final String setName, final int elementCount);
}
