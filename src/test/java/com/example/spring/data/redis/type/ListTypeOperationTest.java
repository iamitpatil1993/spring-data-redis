/**
 * 
 */
package com.example.spring.data.redis.type;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.spring.data.redis.BaseTest;

/**
 * @author amit
 *
 */
@RunWith(SpringRunner.class)
public class ListTypeOperationTest extends BaseTest {

	@Autowired
	private ListTypeOperation<String> listTypeOperation;

	/**
	 * Test method for
	 * {@link com.example.spring.data.redis.type.ListTypeOperation#leftPush(java.lang.Object, java.lang.Object)}.
	 */
	@Test
	public void testLeftPush() {
		// given
		String listKey = UUID.randomUUID().toString();
		String value = UUID.randomUUID().toString();

		// when
		Boolean isPushed = listTypeOperation.leftPush(listKey, value);

		assertThat(isPushed, is(true));
		Optional<String> addedlistItem = listTypeOperation.getAtIndex(listKey, 0);
		assertThat(addedlistItem.isPresent(), is(true));
		assertThat(addedlistItem.get(), is(equalTo(value)));
	}

	/**
	 * Test method for
	 * {@link com.example.spring.data.redis.type.ListTypeOperation#getAtIndex(java.lang.Object, long)}.
	 */
	@Test
	public void testGetAtIndex() {
		// given
		String listKey = UUID.randomUUID().toString();
		String value = UUID.randomUUID().toString();
		listTypeOperation.leftPush(listKey, value);

		// when
		Optional<String> addedlistItem = listTypeOperation.getAtIndex(listKey, 0);
		assertThat(addedlistItem.isPresent(), is(true));
		assertThat(addedlistItem.get(), is(equalTo(value)));
	}

	@Test
	public void testRightPush() {
		// given
		String listKey = UUID.randomUUID().toString();
		String value = UUID.randomUUID().toString();

		// when
		Boolean isPushed = listTypeOperation.rightPush(listKey, value);

		assertThat(isPushed, is(true));
		Optional<String> addedlistItem = listTypeOperation.getAtIndex(listKey, 0);
		assertThat(addedlistItem.isPresent(), is(true));
		assertThat(addedlistItem.get(), is(equalTo(value)));
	}

	@Test
	public void testRightPushBeforeValue() {
		// given
		String listKey = "names";
		listTypeOperation.rightPush(listKey, "John");
		listTypeOperation.rightPush(listKey, "Arya");
		listTypeOperation.rightPush(listKey, "Daenerys");

		// when
		Boolean isPushed = listTypeOperation.rightPushBeforeValue(listKey, "Tyrion", "Arya");

		assertThat(isPushed, is(true));
		Optional<String> addedListItem = listTypeOperation.getAtIndex(listKey, 2);
		assertThat(addedListItem.isPresent(), is(true));
		assertThat(addedListItem.get(), is(equalTo("Tyrion")));
	}

	@Test
	public void testRightPop() {
		// given
		String listKey = "names";
		listTypeOperation.rightPush(listKey, "John");
		listTypeOperation.leftPush(listKey, "Arya");

		// when
		Optional<String> value = listTypeOperation.rightPop(listKey);

		assertThat(value.isPresent(), is(true));
		assertThat(value.get(), is(equalTo("John")));
		assertThat(listTypeOperation.size(listKey).get(), is(1l));
	}

	@Test
	public void testTrim() {
		// given
		String listKey = "names";
		listTypeOperation.rightPush(listKey, "John");
		listTypeOperation.rightPush(listKey, "Arya");
		listTypeOperation.rightPush(listKey, "Eddard");
		listTypeOperation.rightPush(listKey, "Khal");

		// when
		listTypeOperation.trim(listKey, 1, 2);

		Optional<List<String>> listItems = listTypeOperation.getAll(listKey);
		assertThat(listItems.isPresent(), is(true));
		assertThat(listItems.get().size(), is(2));
		assertThat(listItems.get().get(0), is(equalTo("Arya")));
		assertThat(listItems.get().get(1), is(equalTo("Eddard")));
	}

	@Test
	public void testDeleteAtIndex() {
		// given
		String listKey = "names";
		listTypeOperation.rightPush(listKey, "John");
		listTypeOperation.rightPush(listKey, "Arya");
		listTypeOperation.rightPush(listKey, "Eddard");
		listTypeOperation.rightPush(listKey, "Khal");

		// when
		listTypeOperation.deleteAtIndex(listKey, 3);

		Optional<List<String>> listItems = listTypeOperation.getAll(listKey);
		assertThat(listItems.isPresent(), is(true));
		assertThat(listItems.get().size(), is(3));
	}
}
