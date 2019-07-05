package com.example.spring.data.redis.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceConstructor;
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
    @PersistenceConstructor
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

    /**
     * By default, if no-arg constructor is available, spring data redis uses it to create instance and avoid
     * any other constructors.
     * And then populate properties using setters and/or wither methods [for immutable fields].
     */
    public Patient(final UUID id) {
        super(id);
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
