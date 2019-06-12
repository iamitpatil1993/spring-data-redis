/**
 * 
 */
package com.example.spring.data.redis.type;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.spring.data.redis.BaseTest;

/**
 * @author amit
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class StringTypeOperationTest extends BaseTest {

	@Autowired
	private StringTypeOperation stringTypeoperation;

	@Test
	public void testSet() {
		// given
		String key = "foo";
		String value = "bar";

		// when
		stringTypeoperation.set(key, value);

		// then
		assertThat(stringTypeoperation.get(key).isPresent(), is(true));
		assertThat(stringTypeoperation.get(key).get(), is(equalTo(value)));
	}

	@Test
	public void testGet() {
		// given
		String key = "foo";
		String value = "bar";
		stringTypeoperation.set(key, value);

		// when
		Optional<String> result = stringTypeoperation.get(key);

		// then
		assertThat(result.isPresent(), is(true));
		assertThat(result.get(), equalTo(value));
	}

	@Test
	public void testAppend() {
		// given
		String key = "foo";
		String value = "bar";
		stringTypeoperation.set(key, value);

		// when
		boolean isSucceeded = stringTypeoperation.append(key, "foo");

		// then
		assertThat(isSucceeded, is(true));
		assertThat(stringTypeoperation.get(key).get(), is(equalTo(value.concat("foo"))));
	}

	public void testIncrement() {
		// given
		String key = "foo";
		String value = "11231";
		stringTypeoperation.set(key, value);

		// when
		Long increamentedValue = stringTypeoperation.increment(key);

		// then
		assertThat(increamentedValue, is(11232));
	}

	@Test
	public void testDecrement() {
		// given
		String key = "foo";
		String value = "11231";
		stringTypeoperation.set(key, value);

		// when
		Long decreamentedValue = stringTypeoperation.decrement(key);

		// then
		assertThat(decreamentedValue, is(11230l));
	}

	@Test
	public void testDecrementByNumber() {
		// given
		String key = "foo";
		String value = "10";
		stringTypeoperation.set(key, value);

		// when
		Long decreamentedValue = stringTypeoperation.decrement(key, 5);

		// then
		assertThat(decreamentedValue, is(5l));
	}

	@Test
	public void testSubstring() {
		// given
		String key = "name";
		String value = "Amit Patil";
		stringTypeoperation.set(key, value);

		// when
		Optional<String> substring = stringTypeoperation.subString(key, 0, 3);

		// then
		assertThat(substring.isPresent(), is(true));
		assertThat(substring.get(), is(equalTo("Amit")));
	}

	@Test
	public void testGetAndSet() {
		// given
		String key = "name";
		String value = "Foo Bar";
		String newValue = UUID.randomUUID().toString();
		stringTypeoperation.set(key, value);

		// when
		Optional<String> substring = stringTypeoperation.getAndSet(key, newValue);

		// then
		assertThat(substring.get(), is(equalTo(value)));
	}

	@Test
	public void testMultipleGet() {
		// given
		String key = "name";
		String value = "Foo Bar";
		stringTypeoperation.set(key, value);

		String anotherKey = "foo";
		String anotherValue = "Bar";
		stringTypeoperation.set(anotherKey, anotherValue);

		// when
		List<String> values = stringTypeoperation.getMultiple(Arrays.asList(key, anotherKey));

		// then
		assertThat(values, is(not(emptyCollectionOf(String.class))));
		assertThat(values.size(), is(2));
		assertThat(values.get(0), is(equalTo(value)));
		assertThat(values.get(1), is(equalTo(anotherValue)));
	}

	@Test
	public void testSetMultiple() {
		// given
		Map<String, String> map = new HashMap<>(3);
		map.put("foo", "bar");
		map.put("bar", "foo");
		map.put("name", "foo Bar");

		// when
		stringTypeoperation.setmultiple(map);

		// then
		List<String> values = stringTypeoperation.getMultiple(Arrays.asList("foo", "bar", "name"));
		assertThat(values.size(), is(3));
		assertThat(values.get(0), is(equalTo("bar")));
		assertThat(values.get(1), is(equalTo("foo")));
		assertThat(values.get(2), is(equalTo("foo Bar")));
	}

	@Test
	public void testGetLength() {
		// given
		String key = "name";
		String value = UUID.randomUUID().toString();
		stringTypeoperation.set(key, value);

		// when
		long size = stringTypeoperation.getLength(key);

		// then
		assertThat(size, is(36l));
	}

}
