package com.example.spring.data.redis.repository;

import com.example.spring.data.redis.BaseTest;
import com.example.spring.data.redis.model.Gender;
import com.example.spring.data.redis.model.Patient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author amit
 */
@RunWith(SpringRunner.class)
public class PatientRepositoryTest extends BaseTest {

    @Autowired
    private PatientRepository patientRepository;

    /**
     * CrudRepository: save() and findById()
     */
    @Test
    public void testSave() {
        // given
        Patient patient = createTestPatient();

        // when
        patientRepository.save(patient);

        // then
        Optional<Patient> savedPatient = patientRepository.findById(patient.getId());
        assertTrue(savedPatient.isPresent());
        assertThat(savedPatient.get(), is(equalTo(patient)));
    }


    public static Patient createTestPatient() {
        Patient patient = new Patient().withId(UUID.randomUUID());
        patient.setFirstName("Bob");
        patient.setLastName("Sargent");
        patient.setDob(Calendar.getInstance());
        patient.setSsn(UUID.randomUUID().toString());
        patient.setGender(Gender.values()[ThreadLocalRandom.current().nextInt(0, Gender.values().length - 1)]);

        List<String> bloodGroups = Arrays.asList("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+");
        patient.setBloodGroup(bloodGroups.get(ThreadLocalRandom.current().nextInt(0, 8)));
        return patient;
    }
}