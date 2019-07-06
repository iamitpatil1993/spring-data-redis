package com.example.spring.data.redis.configuration;

import com.example.spring.data.redis.model.Patient;
import org.springframework.data.redis.core.index.IndexConfiguration;
import org.springframework.data.redis.core.index.IndexDefinition;
import org.springframework.data.redis.core.index.SimpleIndexDefinition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * If we do not want to clutter our model classes with @Index annotation or We do not have source of Model class
 * in that case we can define redis secondary indexes for Model class using configuration class as well.
 * <p>
 * This class is used to define index on Patient.lastName.
 *
 * @author amit
 */
@Component
public class DefaultIndexConfiguration extends IndexConfiguration {

    @Override
    protected Iterable<? extends IndexDefinition> initialConfiguration() {
        List<IndexDefinition> indexDefinitions = new ArrayList<>(1);

        /*
         we need to provide
            1. Keyspace which is fully-qualified class name by default for Models marked with @RedisHash, so using
                fully qualified class name of patient here.
            2. Path to field/property to be indexed, so passed 'lastName'.
         We can add as define all indexes here by adding to COllection.
        */
        indexDefinitions.add(new SimpleIndexDefinition(Patient.class.getName(), "lastName"));
        return indexDefinitions;
    }
}
