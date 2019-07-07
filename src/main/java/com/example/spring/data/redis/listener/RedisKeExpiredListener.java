package com.example.spring.data.redis.listener;

import com.example.spring.data.redis.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

/**
 * This is patient model specific expiration listener, if we do not set Generic type, this
 * class can be used for expiration listener for all models.
 *
 * @author amit
 */
@Component
public class RedisKeExpiredListener implements ApplicationListener<RedisKeyExpiredEvent<Patient>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisKeExpiredListener.class);
    Jackson2JsonRedisSerializer<Patient> patientJackson2JsonRedisSerializer;

    public RedisKeExpiredListener() {
        patientJackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Patient>(Patient.class);
    }

    /**
     * Unlike MessageListener Message, we get both ke and value (Model instance) using this RedisKeyExpiredEvent.
     *
     * @param event event with Key and model value (expired)
     */
    @Override
    public void onApplicationEvent(RedisKeyExpiredEvent<Patient> event) {
        String key = new String(event.getSource());
        Patient expiredPatient = (Patient) event.getValue();
        String patientStr = new String(patientJackson2JsonRedisSerializer.serialize(expiredPatient));
        LOGGER.info("A Received expire event for key={} with value {}.", key, patientStr);
    }
}
