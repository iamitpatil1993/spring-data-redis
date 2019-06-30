package com.example.spring.data.redis.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.util.Calendar;
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
@EqualsAndHashCode
public class Patient extends BaseEntity {

    private String firstName;

    private String lastName;

    private Calendar dob;

    private String ssn;

    private String bloodGroup;

    private Gender gender;

    public Patient(UUID id, String firstName, String lastName, Calendar dob, String ssn, String bloodGroup,
                   Gender gender) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.ssn = ssn;
        this.bloodGroup = bloodGroup;
        this.gender = gender;
    }

    public Patient() {
        super(null);
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
        return new Patient(id, this.firstName, this.lastName, this.dob, this.ssn, this.bloodGroup, this.gender);
    }
}
