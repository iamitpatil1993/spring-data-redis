package com.example.spring.data.redis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author amit
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    private String addressId;
    private String street;
    private String city;
    private String zip;
    private String state;
    private String country;

    public Address(String street, String city, String zip, String state, String country) {
        this.street = street;
        this.city = city;
        this.zip = zip;
        this.state = state;
        this.country = country;
    }
}
