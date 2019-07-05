package com.example.spring.data.redis.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

/**
 * @author amit
 */
@RedisHash
@Getter
@Setter
public class PatientVital extends BaseEntity {

    private VitalType vital;

    private Double value;

    private UUID patientId;

    @PersistenceConstructor
    public PatientVital(UUID id, VitalType vital, Double value, UUID patientId) {
        super(id);
        this.vital = vital;
        this.value = value;
        this.patientId = patientId;
    }

    public PatientVital(UUID id) {
        super(id);
    }

    public PatientVital withId(final UUID id) {
        return new PatientVital(id, vital, value, patientId);
    }
}
