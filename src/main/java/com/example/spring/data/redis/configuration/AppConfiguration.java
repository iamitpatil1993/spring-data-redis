/**
 *
 */
package com.example.spring.data.redis.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * @author amit
 *
 */

@Configuration
@ComponentScan(basePackages = "com.example.spring.data.redis", excludeFilters = @Filter(classes = Configuration.class))
/*
 order in which we import these does not matter spring manages inter dependencies, rather we should avoid using @Order
 on configuration classes to define custom configuration class load order.
*/
@Import(value = {SpringDataRedisConfiguration.class, TransactionConfiguration.class})
@EnableAspectJAutoProxy
public class AppConfiguration {
    // Nothing to do here for now
}
