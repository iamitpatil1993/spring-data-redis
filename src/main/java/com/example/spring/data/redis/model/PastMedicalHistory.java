package com.example.spring.data.redis.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * @author amit
 */
@Getter
@Setter
public class PastMedicalHistory extends BaseEntity {

    private UUID patientId;

    private String history;

    private PastMedicationHistoryType historyType;

    public PastMedicalHistory(UUID id, String history, PastMedicationHistoryType historyType, UUID patientId) {
        super(id);
        this.history = history;
        this.historyType = historyType;
        this.patientId = patientId;
    }

    /**
     * CustomConverter requires this default constructor, otherwise it will fail to
     * instantiate model during conversion.
     */
    public PastMedicalHistory() {
        super(null);
    }

    public PastMedicalHistory withId(final UUID id) {
        return new PastMedicalHistory(id, history, historyType, patientId);
    }


}
