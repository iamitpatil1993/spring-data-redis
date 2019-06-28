package com.example.spring.data.redis.repository;

import com.example.spring.data.redis.dto.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.UUID;

/**
 * @author amit
 */
@Repository
public class AddressRepositoryImpl implements AddressRepository, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressRepositoryImpl.class);
    private RedisOperations<String, Address> redisOperations;

    @Autowired
    public AddressRepositoryImpl(RedisOperations<String, Address> redisOperations) {
        this.redisOperations = redisOperations;
    }

    /**
     * No need to add @Transactional here. Ideally transaction demarcation should be handled by services
     * above this Repo.
     */
    @Override
    public Address save(Address address) {
        TransactionStatus currentTransactionRef = TransactionAspectSupport.currentTransactionStatus();
        Assert.notNull(currentTransactionRef, "No transaction exists, was expecting one");

        final String addressId = UUID.randomUUID().toString();
        final String addressKey = buildKey(addressId);

        redisOperations.opsForValue().set(addressKey, address);
        address.setAddressId(addressId);
        return address;
    }

    @Override
    public Optional<Address> findById(String addressId) {
        return Optional.ofNullable(redisOperations.opsForValue().get(buildKey(addressId)));
    }

    private String buildKey(final String addressId) {
        return new StringBuffer("address:").append(addressId).toString();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(redisOperations, "RedisOperation injected null ...");
    }
}
