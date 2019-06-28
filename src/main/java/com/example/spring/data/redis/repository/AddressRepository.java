package com.example.spring.data.redis.repository;

import com.example.spring.data.redis.dto.Address;

import java.util.Optional;

/**
 * @author amit
 */
public interface AddressRepository {

    Address save(Address address);

    Optional<Address> findById(final String addressId);
}
