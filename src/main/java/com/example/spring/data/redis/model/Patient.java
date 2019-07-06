package com.example.spring.data.redis.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author amit
 */
/*
 This annotation enables this class to be considered as a Entity for Spring Data repositories.
 By default fully qualified class name is used as a Key prefix in redis for this entity.
*/
@RedisHash
@Getter
@Setter
public class Patient extends BaseEntity {

    /**
     * @Index annotation enables creation of redis secondary index for field.
     * <p>
     * Fields that for which redis secondary index enabled can ONLY be used for finders (Query methods). If field
     * is not indexed and column is used in Query method then spring will not complain but will result not result for
     * criteria applied for that field.
     * </p>
     * <p>
     * So, in order to enable use of field in Query method criteria building, we must enable indexing for those fields.</p>
     */
    @Indexed
    private String firstName;

    // Index for this is defined in configuration DefaultIndexConfiguration config class.
    private String lastName;

    private Calendar dob;

    private String ssn;

    private String bloodGroup;

    private Set<PatientVital> patientVitals = new HashSet<>();

    private Set<String> allergies = new HashSet<>();

    private Set<PastMedicalHistory> pastMedicalHistories = new HashSet<>();

    /*
     Since configured PersistenceConstructor does not set this property, spring will inject this field via field/setter based
     injection. Default is field injection similar to JPA. So, here we will use Setter based injection.
     So, setter will get called to set gender on created Patient instance.
    */
    @AccessType(AccessType.Type.PROPERTY)
    private Gender gender;

    /**
     * Since, this entity now has no default constructor and has two parameterized constructors, it will throw
     * exception as there is ambiguity for which of two parameterized constructors to use.
     * So, either we need to provide default constructor so that spring uses it OR we can declare one of parameterized
     * constructor as a PersistenceConstructor using @PersistenceConstructor, so that spring uses that constructor
     * to instantiate Entity.
     * So, declare this constructor as a PersistenceConstructor.
     * Since, this constructor now accepts all fields, spring will not call wither methods or setters  not reflections to set properties.
     * Hence this approach adds 10% of performance improvement.
     * If any field is missing in this constructor, spring will use setter/wither method for that fields.
     * Read <a href = "here">https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#mapping.object-creation</a>
     */
    public Patient(UUID id, String firstName, String lastName, Calendar dob, String ssn, String bloodGroup,
                   Gender gender, Set<String> allergies, Set<PatientVital> patientVitals,
                   Set<PastMedicalHistory> pastMedicalHistories) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.ssn = ssn;
        this.bloodGroup = bloodGroup;
        this.gender = gender;
        this.allergies = allergies;
        this.patientVitals = patientVitals;
        this.pastMedicalHistories = pastMedicalHistories;
    }

    /**
     * By default, if no-arg constructor is available, spring data redis uses it to create instance and avoid
     * any other constructors.
     * And then populate properties using setters and/or wither methods [for immutable fields].
     */
    public Patient(final UUID id) {
        super(id);
    }

    /**
     * This is another parameterized constructor without GENDER and ID field. So, spring will set gender field via
     * Field/Setter (Field is default which we can override using @AccessType)
     * Since ID field is immutable, spring will use wither method for ID to set ID field.
     */
    @PersistenceConstructor
    public Patient(String firstName, String lastName, Calendar dob, String ssn, String bloodGroup, Set<String> allergies,
                   Set<PatientVital> patientVitals) {
        super(null);
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.ssn = ssn;
        this.bloodGroup = bloodGroup;
        this.allergies = allergies;
        this.patientVitals = patientVitals;
    }

    /**
     * Since we want ID field to be immutable, we can not provide setter for ID field.
     * So, we need to provide wither method for ID field, Which creates new instance of class with provided field value (ID here).
     * Read more about Wither functions. Spring uses Wither fuction to set immutable fields (if setter is not available).
     * See spring data redis docs for more details.
     *
     * @param id id field (immutable)
     * @return New Patient instance with provided ID field.
     */
    public Patient withId(final UUID id) {
        return new Patient(id, this.firstName, this.lastName, this.dob, this.ssn, this.bloodGroup, this.gender,
                allergies, patientVitals, pastMedicalHistories);
    }
}
