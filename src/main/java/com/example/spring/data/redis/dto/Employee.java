package com.example.spring.data.redis.dto;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;

/**
 * @author amit
 */
// This DTO needs to be Serializable in order to use JDK serializer for redis
public class Employee implements Serializable {

    private String employeeId;
    private String firstName;
    private String lastName;
    private Calendar dateOfJoining;

    public Employee() {
    }

    public Employee(String employeeId, String firstName, String lastName, Calendar dateOfJoining) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfJoining = dateOfJoining;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Calendar getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(Calendar dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
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
