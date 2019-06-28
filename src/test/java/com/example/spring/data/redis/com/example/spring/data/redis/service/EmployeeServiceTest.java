package com.example.spring.data.redis.com.example.spring.data.redis.service;

import com.example.spring.data.redis.BaseTest;
import com.example.spring.data.redis.dto.Address;
import com.example.spring.data.redis.dto.Employee;
import com.example.spring.data.redis.repository.AddressRepository;
import com.example.spring.data.redis.repository.EmployeeRepository;
import com.example.spring.data.redis.repository.EmployeeRepositoryImplTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


/**
 * @author amit
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class EmployeeServiceTest extends BaseTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Test
    public void saveEmployeeWithAddress() {
        // given
        String dummyString = UUID.randomUUID().toString();
        Employee employee = EmployeeRepositoryImplTest.buildTestEmployee();
        Address address = new Address(dummyString, dummyString, dummyString, dummyString, dummyString);
        employee.setAddress(address);

        // when
        employeeService.saveEmployeeWithAddress(employee);

        // then
        Optional<Employee> savedEmployee = employeeRepository.get(employee.getEmployeeId());
        Optional<Address> savedAddress = addressRepository.findById(address.getAddressId());
        assertThat(savedAddress.isPresent(), is(true));
        assertThat(savedEmployee.isPresent(), is(true));
    }

    @Test
    public void saveEmployeeWithAddressWithErrorWhileSavingAddress() {
        // given
        String dummyString = UUID.randomUUID().toString();
        Employee employee = EmployeeRepositoryImplTest.buildTestEmployee();
        Address address = new Address(dummyString, dummyString, dummyString, dummyString, dummyString);
        // intentionally setting to null to cause RuntimeException while saving address and transaction to get rollback
        employee.setAddress(null);

        // when
        try {
            employeeService.saveEmployeeWithAddress(employee);
        } catch (Exception e) {
            // Nothing to handle
        }

        // then
        Optional<Employee> savedEmployee = employeeRepository.get(employee.getEmployeeId());
        Optional<Address> savedAddress = addressRepository.findById(address.getAddressId());
        assertThat(savedAddress.isPresent(), is(false));
        assertThat(savedEmployee.isPresent(), is(false));
    }
}