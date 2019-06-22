package com.example.spring.data.redis.repository;

import com.example.spring.data.redis.dto.Employee;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    /**
     * SessioonCallback guarantees that, all operations will be performed inside a 'session',
     * i.e all operations will be performed on single redis connection. Hence, we can
     * use this to perform transactions using redis commands like multi/exec/discard etc.
     * <p>
     * Other callbacks like RedisCallback or operations on redisTemplate may or may not perform
     * operations on same underlying redis connection. So, Session callback suits well where we need
     * to use transacrtion over single connection.
     *
     * @param employee
     * @return
     */
    @Override
    public Employee save(final Employee employee) {
        String employeeId = UUID.randomUUID().toString();
        employee.setEmployeeId(employeeId);

        // SessionCallback is not functional interface, so need to use inner class.
        redisOperations.execute(new SessionCallback<Void>() {
            @Override
            public Void execute(RedisOperations operations) throws DataAccessException {
                operations.multi(); // start transaction
                // save employee
                operations.opsForValue().set(buildKey(employeeId), employee);

                // Save skills [Intentionally not having null/empty check on skills]
                String[] skills = employee.getSkills().toArray(new String[employee.getSkills().size()]);
                operations.opsForSet().add(buildSkillsKey(employeeId), skills);

                operations.exec(); // execute transaction
                return null;
            }
        });
        return employee;
    }

    @Override
    public int count() {
        // avoid using Keys in production over SCAN, but unfortunately RedisTemplate do not support SCAN,
        // and we need to use RedisConnection.
        // refer https://docs.spring.io/spring-data/redis/docs/2.1.8.RELEASE/reference/html/#appendix:command-reference
        Set<String> keys = redisOperations.keys("employees:*");
        return keys.stream().filter(key -> !key.endsWith("skills")).collect(Collectors.toList()).size();
    }

    @Override
    public Set<String> getSkillsByEmployeeId(final String employeeId) {
        if (employeeId == null || employeeId.isEmpty()) {
            throw new RuntimeException("Empty empolyeeId while getting skills by emloyee");
        }
        return redisOperations.execute((RedisConnection connection) -> {
            StringRedisConnection stringRedisConnection = new DefaultStringRedisConnection(connection);
            return stringRedisConnection.sMembers(buildSkillsKey(employeeId));
        });
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

    /**
     * build key for employee skills
     *
     * @param employeeId
     * @return
     */
    private String buildSkillsKey(String employeeId) {
        return buildKey(employeeId).concat(":skills");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        assert redisOperations != null : "Injected null redis template";
        valueOperations = redisOperations.opsForValue();
    }
}
