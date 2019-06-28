package com.example.spring.data.redis.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

/**
 * @author amit
 */
// This DTO needs to be Serializable in order to use JDK serializer for redis
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class Employee implements Serializable {

    private String employeeId;
    private String firstName;
    private String lastName;

    private Calendar dateOfJoining;
    private Integer age = 32;

    private Date createdDate = new Date();

    private Date updatedDate = new Date();

    @JsonIgnore
    private Set<String> skills = new HashSet<>();
    private Address address;

    public Employee() {
    }

    public Employee(String employeeId, String firstName, String lastName, Calendar dateOfJoining) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfJoining = dateOfJoining;
    }


    public void addSkill(final String skill) {
        skills.add(skill);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(getEmployeeId(), employee.getEmployeeId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmployeeId());
    }
}
