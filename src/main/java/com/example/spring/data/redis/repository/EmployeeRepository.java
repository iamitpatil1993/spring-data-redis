package com.example.spring.data.redis.repository;

import com.example.spring.data.redis.dto.Employee;

import java.util.Optional;
import java.util.Set;

/**
 * @author amit
 */
public interface EmployeeRepository {

    Employee save(Employee employee);

    Set<String> getSkillsByEmployeeId(String employeeId);

    Optional<Employee> get(final String employeeId);

    int count();
}
