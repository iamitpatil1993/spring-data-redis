package com.example.spring.data.redis.repository;

import com.example.spring.data.redis.dto.Employee;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author amit
 */
@Repository
public class EmployeeRepositoryImpl implements InitializingBean, EmployeeRepository {

    private RedisOperations<String, Employee> redisOperations;
    private ValueOperations<String, Employee> valueOperations;

    @Autowired
    public EmployeeRepositoryImpl(@Qualifier("employeeRedisTemplate") RedisOperations<String, ?> redisTemplate) {
        this.redisOperations = (RedisOperations<String, Employee>) redisTemplate;
    }

    @Override
    public Employee save(final Employee employee) {
        String employeeId = UUID.randomUUID().toString();
        employee.setEmployeeId(employeeId);
        valueOperations.set(buildKey(employeeId), employee);
        return employee;
    }

    @Override
    public Optional<Employee> get(String employeeId) {
        return Optional.ofNullable(valueOperations.get(buildKey(employeeId)));
    }

    /**
     * Buils key for redis by employeeId.
     *
     * @param employeeId
     * @return Redis key for employee.
     */
    private String buildKey(final String employeeId) {
        return new StringBuilder("employees:").append(employeeId).toString();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        assert redisOperations != null : "Injected null redis template";
        valueOperations = redisOperations.opsForValue();
    }
}
