package com.example.spring.data.redis;

import com.example.spring.data.redis.dto.Address;
import com.example.spring.data.redis.dto.Employee;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author amit
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class HashBasedEmployeeRepositoryTest extends BaseTest {

    @Autowired
    private HashBasedEmployeeRepository employeeRepository;

    @Test
    public void save() {
        Employee employee = createDummyEmployee();

        // when
        Employee savedEmployee = employeeRepository.save(employee);

        // then
        assertThat(savedEmployee, is(notNullValue()));
    }


    @Test
    public void testGet() {
        // given
        Employee dummyEmployee = createDummyEmployee();
        employeeRepository.save(dummyEmployee);

        // when
        Optional<Employee> employee = employeeRepository.get(dummyEmployee.getEmployeeId());

        // then
        assertThat(employee, is(notNullValue()));
        assertThat(employee.isPresent(), is(true));
    }

    private Employee createDummyEmployee() {
        final String dummyString = UUID.randomUUID().toString();
        Employee employee = new Employee(dummyString, dummyString, dummyString, Calendar.getInstance());
        Address address = new Address(dummyString, dummyString, dummyString, dummyString, dummyString);
        employee.addSkill(dummyString);
        employee.setAddress(address);
        return employee;
    }
}