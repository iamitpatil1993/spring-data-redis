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

    private String street;
    private String city;
    private String zip;
    private String state;
    private String country;
}
