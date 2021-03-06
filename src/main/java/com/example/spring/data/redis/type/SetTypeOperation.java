package com.example.spring.data.redis.type;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.Set;

/**
 * @author amit
 */
@Component
public class SetTypeOperation<V> implements InitializingBean {

    private RedisTemplate<String, V> redisOperation;

    /**
     * We can inject SetOperations directly using RedisTemplate. Spring handles this for us.
     * We just need to provide RedisTemplate bean name, and spring will call appropriate method
     * on it to get target Operation type. [here SetOperations]
     * We can not use @Autowired annotation here for same purpose. As it does not takes beanId/beanName.
     * In order to inject bean by name we need to use @Qualifier in case of @Autowired, which we
     * can not use here, as we do not want to inject exact bean by qualifier/id.
     * So, no need to inject RedisTemplate if we only want operations on Set.
     */
    @Resource(name = "redisTemplateWithJdkSerializer")
    private SetOperations<String, V> setOperations;

    @Autowired
    public SetTypeOperation(RedisTemplate<String, V> redisOperation) {
        this.redisOperation = redisOperation;
    }

    public long add(final String key, final V... setItem) {
        return setOperations.add(key, setItem);
    }

    public Optional<Long> size(final String key) {
        Optional<Long> size = Optional.empty();
        if (redisOperation.hasKey(key)) {
            return Optional.of(setOperations.size(key));
        }
        return size;
    }

    public boolean contains(final String key, final V value) {
        return setOperations.isMember(key, value);
    }

    public Optional<Set<V>> getAll(final String key) {
        return Optional.ofNullable(setOperations.members(key));
    }

    public boolean remove(final String key, final V valueToRemove) {
        return setOperations.remove(key, valueToRemove) > 0;
    }

    public boolean createCopy(final String copyFromKey, final String copyIntoKey) {
        return setOperations.unionAndStore(copyFromKey, copyFromKey, copyIntoKey) > 0;
    }

    public Set<V> diff(final String fromKey, final String toKey) {
        return setOperations.difference(fromKey, toKey);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        assert redisOperation != null : "Redis template injected null ....";
        assert setOperations != null : "Redis Set operations injected null ....";
    }
}
