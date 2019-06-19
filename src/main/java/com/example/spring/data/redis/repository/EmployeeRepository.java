package com.example.spring.data.redis.repository;

import com.example.spring.data.redis.dto.Employee;

import java.util.Optional;

/**
 * @author amit
 */
public interface EmployeeRepository {

    Employee save(Employee employee);

    Optional<Employee> get(final String employeeId);
}
