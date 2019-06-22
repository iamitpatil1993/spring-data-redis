package com.example.spring.data.redis.repository;

import com.example.spring.data.redis.BaseTest;
import com.example.spring.data.redis.dto.Employee;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
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
}