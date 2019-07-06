package com.example.spring.data.redis.model.converter;

import com.example.spring.data.redis.model.PastMedicalHistory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * Converter that converts byte[] read from redis into PatientMedicalHistory model.
 * We can have any custom conversion logic in this converter.
 * I have just jackson serializer, but conversion logic can be anything, as long as we provide opposite
 * converter as well.
 * <p>
 * NOTE: NO need to declare this class as a spring bean, since we will wrap this class inside CustomConverter object,
 * which will be a spring bean declared in configuration.
 *
 *  @author amit
 */
public class BytesToPastMedicalHistoryConverter implements Converter<byte[], PastMedicalHistory> {

    private Jackson2JsonRedisSerializer<PastMedicalHistory> serializer;

    public BytesToPastMedicalHistoryConverter() {
        this.serializer = new Jackson2JsonRedisSerializer<>(PastMedicalHistory.class);
    }

    /**
     * Any conversion logic can be used here.
     */
    @Override
    public PastMedicalHistory convert(byte[] source) {
        return serializer.deserialize(source);
    }
}
