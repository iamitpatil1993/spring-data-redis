package com.example.spring.data.redis.repository;

import com.example.spring.data.redis.BaseTest;
import com.example.spring.data.redis.dto.Employee;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;


/**
 * @author amit
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class EmployeeRepositoryImplTest extends BaseTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void testSave() {
        // given
        Employee employee = buildTestEmployee();

        // when
        Employee persistentEmployee = employeeRepository.save(employee);

        // then
        assertThat(persistentEmployee.getEmployeeId(), is(notNullValue()));
        Optional<Employee> result = employeeRepository.get(persistentEmployee.getEmployeeId());
        assertThat(result.isPresent(), is(true));
        assertTrue(employee.equals(result.get()));

        Set<String> employeeSkills = employeeRepository.getSkillsByEmployeeId(persistentEmployee.getEmployeeId());
        assertNotNull(employeeSkills);
        assertEquals(employeeSkills.size(), employee.getSkills().size());
    }

    @Test
    public void testSaveWithErrorWhileSavingSkills() {
        // given
        Employee employee = buildTestEmployee();
        employee.setSkills(null); // setting skills to null, to cause transaction to faill

        // when
        try {
            Employee persistentEmployee = employeeRepository.save(employee);
        } catch (Exception e) {
            // Nothing to do here.
        }

        // then
        // this assertion says that, error while saving skills will rollback transaction,
        // hence employee also will not be saved
        assertThat(employeeRepository.count(), is(0));
    }

    private Employee buildTestEmployee() {
        Employee employee = new Employee();
        employee.setEmployeeId(UUID.randomUUID().toString());
        employee.setFirstName(UUID.randomUUID().toString());
        employee.setLastName(UUID.randomUUID().toString());
        employee.setDateOfJoining(Calendar.getInstance());

        Set<String> skills = new HashSet<>(2);
        skills.add(UUID.randomUUID().toString());
        skills.add(UUID.randomUUID().toString());
        employee.setSkills(skills);
        return employee;
    }

    @Test
    public void testFindAll() {
        // given
        int count = 5000;
        List<Employee> employees = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            employees.add(buildTestEmployee());
        }
        employeeRepository.saveMultiple(employees);

        // when
        List<Employee> result = employeeRepository.findAll();

        // then
        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(equalTo(count)));
    }

    /**
     * Transaction Part-1: Test to update employee with Declarative transaction.
     * Positive case.
     * Expected: Should save/update both employee details and skills in transaction.
     */
    @Test
    public void testUpdate() {
        // given
        String dummyString = UUID.randomUUID().toString();
        Employee employee = buildTestEmployee();
        Employee persistentEmployee = employeeRepository.save(employee);

        // when
        persistentEmployee.setFirstName(dummyString);
        persistentEmployee.setLastName(dummyString);

        Set<String> skills = new HashSet<>(2);
        skills.add(dummyString);
        persistentEmployee.setSkills(skills);
        employeeRepository.update(employee);

        // then
        Optional<Employee> updatedEmployee = employeeRepository.get(persistentEmployee.getEmployeeId());
        assertThat(updatedEmployee.isPresent(), is(true));
        assertThat(updatedEmployee.get().getFirstName(), is(equalTo(persistentEmployee.getFirstName())));
        assertThat(updatedEmployee.get().getLastName(), is(equalTo(persistentEmployee.getLastName())));

        Set<String> employeeSkills = employeeRepository.getSkillsByEmployeeId(persistentEmployee.getEmployeeId());
        assertNotNull(employeeSkills);
        assertEquals(employeeSkills.size(), persistentEmployee.getSkills().size());
        assertThat(employeeSkills, hasItem(dummyString));
    }

    /**
     * Transaction Part-1: Test to update employee with Declarative transaction.
     * Negative case: Will intentionally try to fail skills save operation to rollback transaction.
     * Expected: Should rollback transaction and should rollback employee details save operation.
     */
    @Test
    public void testUpdateWithErrorWhileSavingSkills() {
        // given
        String dummmyString = UUID.randomUUID().toString();
        Employee employee = buildTestEmployee();
        Employee persistentEmployee = employeeRepository.save(employee);

        // when
        persistentEmployee.setFirstName(dummmyString);
        persistentEmployee.setLastName(dummmyString);

        /*
         Intentionally setting to null to cause runtime exception (NullPointer) in transactional method, so that
         transaction get rollback
        */
        persistentEmployee.setSkills(null);
        try {
            employeeRepository.update(employee);
        } catch (Exception e) {
            // Nothing to handler
        }

        // then
        Optional<Employee> nonUpdatedEmployee = employeeRepository.get(persistentEmployee.getEmployeeId());
        assertThat(nonUpdatedEmployee.isPresent(), is(true));
        assertThat(nonUpdatedEmployee.get().getFirstName(), is(not(equalTo(persistentEmployee.getFirstName()))));
        assertThat(nonUpdatedEmployee.get().getLastName(), is(not(equalTo(persistentEmployee.getLastName()))));

        Set<String> employeeSkills = employeeRepository.getSkillsByEmployeeId(persistentEmployee.getEmployeeId());
        assertNotNull(employeeSkills);
        assertThat(employeeSkills.size(), is(greaterThan(0)));
    }
}