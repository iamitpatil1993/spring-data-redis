package com.example.spring.data.redis.repository;

import com.example.spring.data.redis.BaseTest;
import com.example.spring.data.redis.dto.Employee;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


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
        Employee employee = new Employee();
        employee.setEmployeeId(UUID.randomUUID().toString());
        employee.setFirstName(UUID.randomUUID().toString());
        employee.setLastName(UUID.randomUUID().toString());
        employee.setDateOfJoining(Calendar.getInstance());

        // when
        Employee persistentEmployee = employeeRepository.save(employee);

        // then
        assertThat(persistentEmployee.getEmployeeId(), is(notNullValue()));
        Optional<Employee> result = employeeRepository.get(persistentEmployee.getEmployeeId());
        assertThat(result.isPresent(), is(true));
        assertTrue(employee.equals(result.get()));
    }
}