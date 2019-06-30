package com.example.spring.data.redis.repository;

import com.example.spring.data.redis.model.Patient;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * @author amit
 */

/**
 * This is normal spring data repository as we do in JPA. There is nothing special to indicate that this is
 * spring data redis specific repository.
 */
public interface PatientRepository extends CrudRepository<Patient, UUID> {
    // No custom repository methods for now
}
