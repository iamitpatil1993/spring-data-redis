package com.example.spring.data.redis;

import org.springframework.stereotype.Component;

@Component
public class ExampleService implements Service {
	
	/**
	 * Reads next record from input
	 */
	public String getMessage() {
		return "Hello world!";	
	}

}
