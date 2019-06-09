/**
 * 
 */
package com.example.spring.data.redis.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;

/**
 * @author amit
 *
 */

@Configuration
@ComponentScan(basePackages = "com.example.spring.data.redis", excludeFilters = @Filter(classes = Configuration.class))
public class AppConfiguration {
	// Nothing to do here for now
}
