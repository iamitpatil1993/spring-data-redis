package com.example.spring.data.redis.repository;

import com.example.spring.data.redis.dto.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author amit
 */
@Repository
@Primary
public class EmployeeRepositoryImpl implements InitializingBean, EmployeeRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeRepositoryImpl.class);
    public static final String PATTERN_EMPLOYEES_GET_ALL = "employees:*[^skills]";
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

    /**
     * SCAN is always prefered over KEYS, as KEYS may block redis server.
     *
     * @return
     * @see <a href="https://stackoverflow.com/questions/32603964/scan-vs-keys-performance-in-redis">SCAN Vs KEYS Redis</a>
     */
    @Override
    public List<Employee> findAll() {
        // find all employee keys using SCAN operation.
        List<String> keys = redisOperations.execute((RedisConnection connection) -> {
            List<String> employeeKeys = new ArrayList<>();
            Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(PATTERN_EMPLOYEES_GET_ALL).build());
            while (cursor.hasNext()) {
                employeeKeys.add(new String(cursor.next()));
            }
            LOGGER.info("Found {} employees", employeeKeys.size());
            return employeeKeys;
        });
        if (keys.isEmpty()) {
            return Collections.emptyList();
        }
        // get employees by keys
        return redisOperations.opsForValue().multiGet(keys);
    }

    @Override
    public List<Employee> saveMultiple(List<Employee> employees) {
        if (employees == null || employees.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, Employee> employeeMap = new HashMap<>(employees.size());
        employees.stream().forEach(emp -> {
            emp.setEmployeeId(UUID.randomUUID().toString());
            employeeMap.put(buildKey(emp.getEmployeeId()), emp);
        });
        valueOperations.multiSet(employeeMap);
        return employees;
    }

    /**
     * Similar to spring data jdbc, we can annotate the methods with @Transactional annotation and
     * spring will handle transaction using provided transaction manager and aspects.
     */
    @Override
    @Transactional
    public void update(final Employee employee) {
        final String employeeKey = buildKey(employee.getEmployeeId());
        // update employee info
        redisOperations.opsForValue().set(employeeKey, employee);

        // update employee skills
        redisOperations.execute((RedisConnection connection) -> {
            byte[] skillsKey = buildSkillsKey(employee.getEmployeeId()).getBytes();
            // delete existing skills
            connection.del(skillsKey);

            // update new skills
            employee.getSkills().stream().map(String::getBytes).forEach(skill -> connection.sAdd(skillsKey, skill));
            return null;
        });
    }

    /**
     * No need to add @Transactional here. Ideally transaction demarcation should be handled by services
     * above this Repo.
     * created this method because existing method was creating transaction using SessionCallback. And nested
     * transaction is not supported.
     */
    @Override
    public Employee saveWithoutTransaction(final Employee employee) {
        TransactionStatus currentTransactionRef = TransactionAspectSupport.currentTransactionStatus();
        Assert.notNull(currentTransactionRef, "No transaction exists, was expecting one");

        String employeeId = UUID.randomUUID().toString();
        employee.setEmployeeId(employeeId);

        // save employee
        redisOperations.opsForValue().set(buildKey(employeeId), employee);

        // Save skills
        redisOperations.execute((RedisConnection connection) -> {
            byte[] skillsKey = buildSkillsKey(employee.getEmployeeId()).getBytes();
            employee.getSkills().stream().map(String::getBytes).forEach(skill -> connection.sAdd(skillsKey, skill));
            return null;
        });
        return employee;
    }
}
