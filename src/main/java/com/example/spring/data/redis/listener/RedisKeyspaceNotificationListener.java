package com.example.spring.data.redis.listener;

import com.example.spring.data.redis.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * This is generic event listener to listen all keyspace events in redis.
 * In practice, we should not have generic listener like this, rather should define different listeners for different
 * data structures or operations or keys etc.
 * <p>
 * We can use this generic listener only when we do not want to perform any Type (Model) specific activities or operation
 * specific activities, rather want to do generic activities like audit logging etc.
 *
 * @author amit
 */
@Component
public class RedisKeyspaceNotificationListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisKeyspaceNotificationListener.class);

    private PatientRepository patientRepository;

    @Autowired
    public RedisKeyspaceNotificationListener(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Spring creates phantom copy of each repository entity saved in redis. Which has ttl 5 minutes more than one
     * set of key. So, phantom entry is available even after main entry is expired. Which we can use in this listener.
     * <p>
     * NOTE: We can not get operation and can only get key. To distinguish operations, we should define operation
     * specific seperate listeners.
     *
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String eventBody = new String(message.getBody());
        String patternString = new String(pattern);
        LOGGER.trace("eventBody is :: {}, pattern :: {}", eventBody, patternString);
    }
}






