package com.example.spring.data.redis;

import com.example.spring.data.redis.dto.Employee;
import com.example.spring.data.redis.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @author amit
 */
@Repository
public class HashBasedEmployeeRepository implements EmployeeRepository, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(HashBasedEmployeeRepository.class);

    private HashOperations<String, String, String> hashOperations;
    private HashMapper<Employee, String, String> hashMapper;
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public HashBasedEmployeeRepository(@Qualifier("stringTemplate") StringRedisTemplate stringRedisTemplate,
                                       HashMapper<Employee, String, String> hashMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.hashMapper = hashMapper;
        this.hashOperations = stringRedisTemplate.opsForHash();
    }

    @Override
    public Employee save(Employee employee) {
        employee.setEmployeeId(UUID.randomUUID().toString());
        Map<String, String> employeeHash = hashMapper.toHash(employee);
        hashOperations.putAll(buildKey(employee.getEmployeeId()), employeeHash);
        return employee;
    }

    @Override
    public Set<String> getSkillsByEmployeeId(String employeeId) {
        return null;
    }

    @Override
    public Optional<Employee> get(String employeeId) {
        final String employeeKy = buildKey(employeeId);
        Map<String, String> employeeHash = hashOperations.entries(employeeKy);
        if (employeeHash == null) {
            return Optional.empty();
        }
        return Optional.of(hashMapper.fromHash(employeeHash));
    }

    @Override
    public int count() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<Employee> findAll() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<Employee> saveMultiple(List<Employee> employees) {
        throw new RuntimeException("Not implemented");
    }

    private String buildKey(final String employeeId) {
        return new StringBuilder("employees:").append(employeeId).toString();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(stringRedisTemplate, "Null redis connection factory injected ...");
        Assert.notNull(hashMapper, "Null HashMapper injected ...");
    }
}
