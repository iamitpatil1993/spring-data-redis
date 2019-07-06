package com.example.spring.data.redis.repository;

import com.example.spring.data.redis.BaseTest;
import com.example.spring.data.redis.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

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

    @Test
    public void testCount() {
        // given
        Patient patient = createTestPatient();
        patientRepository.save(patient);

        // when
        long patientCount = patientRepository.count();

        // then
        assertThat(patientCount, is(equalTo(1l)));
    }

    @Test
    public void testDeleteByEntity() {
        // given
        Patient patient = createTestPatient();
        patientRepository.save(patient);

        // when
        patientRepository.delete(patient);

        // then
        assertThat(patientRepository.findById(patient.getId()).isPresent(), is(false));
    }

    @Test
    public void testDeleteById() {
        // given
        Patient patient = createTestPatient();
        patientRepository.save(patient);

        // when
        patientRepository.deleteById(patient.getId());

        // then
        assertThat(patientRepository.findById(patient.getId()).isPresent(), is(false));
    }

    @Test
    public void testFindAll() {
        // given
        List<Patient> patientsToSave = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            patientsToSave.add(createTestPatient());
        }
        patientRepository.saveAll(patientsToSave);

        // when
        List<Patient> result = (List<Patient>) patientRepository.findAll();

        // then
        assertThat(result.size(), is(10));
    }

    @Test
    public void testSavePatientWithPatientVitals() {
        // given
        Patient patient = createTestPatient();
        Set<PatientVital> patientVitals = new HashSet<>(3);

        for (int i = 0; i < 3; i++) {
            patientVitals.add(createTestPatientVial(patient));
        }
        patient.setPatientVitals(patientVitals);

        // when
        patientRepository.save(patient);

        // then
        Optional<Patient> patientByIdResult = patientRepository.findById(patient.getId());
        assertThat(patientByIdResult.isPresent(), is(true));
        assertEquals(patient, patientByIdResult.get());

        assertThat(patientByIdResult.get().getPatientVitals().size(), is(equalTo(patient.getPatientVitals().size())));
        patient.getPatientVitals().stream().forEach(patientVital -> {
            assertTrue(patientByIdResult.get().getPatientVitals().contains(patientVital));
        });
    }

    @Test
    public void testSavePatientWithAllergiesAndPatientVitals() {
        // given
        Patient patient = createTestPatient();
        Set<String> allergies = new HashSet<>(3);
        Set<PatientVital> patientVitals = new HashSet<>(3);

        for (int i = 0; i < 3; i++) {
            allergies.add(UUID.randomUUID().toString());
            patientVitals.add(createTestPatientVial(patient));
        }
        patient.setAllergies(allergies);
        patient.setPatientVitals(patientVitals);


        // when
        patientRepository.save(patient);

        // then
        Optional<Patient> patientByIdResult = patientRepository.findById(patient.getId());
        assertThat(patientByIdResult.isPresent(), is(true));
        assertEquals(patient, patientByIdResult.get());

        assertThat(patientByIdResult.get().getPatientVitals().size(), is(equalTo(patient.getPatientVitals().size())));
        patient.getPatientVitals().stream().forEach(patientVital -> {
            assertTrue(patientByIdResult.get().getPatientVitals().contains(patientVital));
        });

        assertThat(patientByIdResult.get().getAllergies().size(), is(equalTo(patient.getAllergies().size())));
        patient.getAllergies().stream().forEach(allergy -> {
            assertTrue(patientByIdResult.get().getAllergies().contains(allergy));
        });
    }

    @Test
    public void testSavePatientWithAllergiesAndPatientVitalsAndPastMedicalHistories() {
        // given
        int count = 3;
        Patient patient = createTestPatient();
        Set<String> allergies = new HashSet<>(count);
        Set<PatientVital> patientVitals = new HashSet<>(count);
        Set<PastMedicalHistory> pastMedicalHistories = new HashSet<>(count);
        pastMedicalHistories.add(createTestPastMedicalHistory(patient, PastMedicationHistoryType.ADOLESCENCE));
        pastMedicalHistories.add(createTestPastMedicalHistory(patient, PastMedicationHistoryType.CHILDHOOD));
        pastMedicalHistories.add(createTestPastMedicalHistory(patient, PastMedicationHistoryType.IMMUNIZATION));

        for (int i = 0; i < count; i++) {
            allergies.add(UUID.randomUUID().toString());
            patientVitals.add(createTestPatientVial(patient));
        }
        patient.setAllergies(allergies);
        patient.setPatientVitals(patientVitals);
        patient.setPastMedicalHistories(pastMedicalHistories);

        // when
        patientRepository.save(patient);

        // then
        Optional<Patient> patientByIdResult = patientRepository.findById(patient.getId());
        assertThat(patientByIdResult.isPresent(), is(true));
        assertEquals(patient, patientByIdResult.get());

        assertThat(patientByIdResult.get().getPatientVitals().size(), is(equalTo(patient.getPatientVitals().size())));
        patient.getPatientVitals().stream().forEach(patientVital -> {
            assertTrue(patientByIdResult.get().getPatientVitals().contains(patientVital));
        });

        assertThat(patientByIdResult.get().getAllergies().size(), is(equalTo(patient.getAllergies().size())));
        patient.getAllergies().stream().forEach(allergy -> {
            assertTrue(patientByIdResult.get().getAllergies().contains(allergy));
        });

        assertThat(patientByIdResult.get().getPastMedicalHistories().size(),
                is(equalTo(patient.getPastMedicalHistories().size())));
        patient.getPastMedicalHistories().stream().forEach(pastMedicalHistory -> {
            assertTrue(patientByIdResult.get().getPastMedicalHistories().contains(pastMedicalHistory));
        });

    }


    public static Patient createTestPatient() {
        Patient patient = new Patient(UUID.randomUUID());
        patient.setFirstName("Bob");
        patient.setLastName("Sargent");
        patient.setDob(Calendar.getInstance());
        patient.setSsn(UUID.randomUUID().toString());
        patient.setGender(Gender.values()[ThreadLocalRandom.current().nextInt(0, Gender.values().length - 1)]);

        List<String> bloodGroups = Arrays.asList("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+");
        patient.setBloodGroup(bloodGroups.get(ThreadLocalRandom.current().nextInt(0, 8)));
        return patient;
    }


    public static PatientVital createTestPatientVial(Patient patient) {
        PatientVital patientVital = new PatientVital(UUID.randomUUID());
        patientVital.setPatientId(patient.getId());
        patientVital.setValue(2323d);
        patientVital.setVital(VitalType.HEIGHT);
        return patientVital;
    }

    public static PastMedicalHistory createTestPastMedicalHistory(Patient patient, final PastMedicationHistoryType historyType) {
        return new PastMedicalHistory(UUID.randomUUID(), UUID.randomUUID().toString(), historyType, patient.getId());
    }

    @Test
    public void testFindByFirstNameAndLastName() {
        // given
        Patient patient = createTestPatient();
        patientRepository.save(patient);

        patient = createTestPatient();
        patientRepository.save(patient);

        // when
        List<Patient> allByFirstNameAndLastName = patientRepository.findAllByFirstNameAndLastName(patient.getFirstName(),
                patient.getLastName());

        // then
        assertThat(allByFirstNameAndLastName.size(), is(equalTo(2)));
        assertThat(allByFirstNameAndLastName.get(0).getFirstName(), is(equalTo(patient.getFirstName())));
        assertThat(allByFirstNameAndLastName.get(0).getLastName(), is(equalTo(patient.getLastName())));
    }
}