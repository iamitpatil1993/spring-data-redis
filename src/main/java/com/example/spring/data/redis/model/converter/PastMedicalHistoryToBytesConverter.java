package com.example.spring.data.redis.model.converter;

import com.example.spring.data.redis.model.PastMedicalHistory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * Converter that converts PatientMedicalHistory into byte[] before writing to redis.
 * We can have any custom conversion logic in this converter.
 * I have just jackson deserializer, but conversion logic can be anything, as long as we provide opposite
 * converter as well.
 * <p>
 * NOTE: NO need to declare this class as a spring bean, since we will wrap this class inside CustomConverter object,
 * which will be a spring bean declared in configuration.
 *
 * NOTE: This converter converting model into single value (byte[]) and not Hash, but we can do that as well
 *       using this converter.
 *       Just replace Converter<PastMedicalHistory, byte[]> to Converter<PastMedicalHistory, Map<String, byte[]>>
 *       which will convert model to map. (Obliviously we need to write that conversion logic)
 *
 * @author amit
 */
public class PastMedicalHistoryToBytesConverter implements Converter<PastMedicalHistory, byte[]> {

    private Jackson2JsonRedisSerializer<PastMedicalHistory> serializer;

    public PastMedicalHistoryToBytesConverter() {
        this.serializer = new Jackson2JsonRedisSerializer<>(PastMedicalHistory.class);
    }

    @Override
    public byte[] convert(PastMedicalHistory source) {
        return serializer.serialize(source);
    }
}
