package com.example.spring.data.redis.repository;

import com.example.spring.data.redis.model.Patient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author amit
 */

/**
 * This is normal spring data repository as we do in JPA. There is nothing special to indicate that this is
 * spring data redis specific repository.
 * <p>
 * Spring repository support very limited query method functionalitie as compared to JPA.
 */
public interface PatientRepository extends CrudRepository<Patient, UUID>, QueryByExampleExecutor<Patient> {

    List<Patient> findAllByFirstName(final String firstName);

    /**
     * Since firstName and lastName fields are Indexed, we can use them to define criteria in Query methods.
     */
    List<Patient> findAllByFirstNameAndLastName(final String firstName, final String lastName);

    /****
     * Since, ssn field is not indexed, spring will not throw any error but will return empty result.
     */
    Optional<Patient> findBySsn(final String ssn);
}
