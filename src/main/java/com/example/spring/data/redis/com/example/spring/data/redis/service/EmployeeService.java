package com.example.spring.data.redis.com.example.spring.data.redis.service;

import com.example.spring.data.redis.dto.Employee;
import com.example.spring.data.redis.repository.AddressRepository;
import com.example.spring.data.redis.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.Assert;

/**
 * @author amit
 */
@Service
public class EmployeeService implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);
    private EmployeeRepository employeeRepository;

    private AddressRepository addressRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, AddressRepository addressRepository) {
        this.employeeRepository = employeeRepository;
        this.addressRepository = addressRepository;
    }


    @Transactional
    public void saveEmployeeWithAddress(Employee employee) {
        TransactionStatus currentTransactionRef = TransactionAspectSupport.currentTransactionStatus();
        Assert.notNull(currentTransactionRef, "No transaction exists, was expecting one");

        employeeRepository.saveWithoutTransaction(employee);

        /*
         Address repository is using different instance of RedisTemplate, still spring manages the transaction
         between two different redis templates.
         Even though Address repository uses different RedisTemplate, it participates in current transaction
         created here.
         NOTE: Both RedisTemplate used in EmployeeRepository and RedisTemplate used in AddressRepository MUST
         enable transaction on them (using setEnableTransactionSupport(true)) individually in order to support
          transactions and participate in existing transactions.
        */
        addressRepository.save(employee.getAddress());

        LOGGER.info("Employee saved successfully with address ...");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(employeeRepository, "EmployeeRepository injected null");
        Assert.notNull(employeeRepository, "AddressRepository injected null");
    }
}
