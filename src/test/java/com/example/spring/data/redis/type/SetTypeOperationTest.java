package com.example.spring.data.redis.type;

import com.example.spring.data.redis.BaseTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author amit
 */
@RunWith(SpringRunner.class)
public class SetTypeOperationTest extends BaseTest {

    @Autowired
    private SetTypeOperation<String> setTypeOperation;

    @Test
    public void add() {
        // given
        String setName = "tags";
        String item = UUID.randomUUID().toString();

        // when
        long added = setTypeOperation.add(setName, item);

        assertThat(added, is(1l));
        assertThat(setTypeOperation.size(setName).get(), equalTo(1l));
        assertThat(setTypeOperation.contains(setName, item), is(true));
    }


    @Test
    public void testGetAll() {
        // given
        String setName = "tags";
        setTypeOperation.add(setName, UUID.randomUUID().toString()
                , UUID.randomUUID().toString(), UUID.randomUUID().toString());


        // when
        Optional<Set<String>> members = setTypeOperation.getAll(setName);

        // then
        assertThat(members.isPresent(), is(true));
        assertThat(members.get().size(), is(equalTo(3)));
    }

    @Test
    public void testRemove() {
        String setName = "tags";
        String itemToRemove = UUID.randomUUID().toString();
        setTypeOperation.add(setName, UUID.randomUUID().toString()
                , UUID.randomUUID().toString(), UUID.randomUUID().toString(), itemToRemove);

        // when
        boolean isRemoved = setTypeOperation.remove(setName, itemToRemove);

        // then
        assertThat(isRemoved, is(true));
        assertThat(setTypeOperation.contains(setName, itemToRemove), is(false));

    }

    @Test
    public void testCreateCopy() {
        String setName = "tags";
        List<String> tags = Arrays.asList(UUID.randomUUID().toString()
                , UUID.randomUUID().toString(), UUID.randomUUID().toString());
        setTypeOperation.add(setName, tags.toArray(new String[tags.size()]));

        // when
        String copySetName = "tags2";
        setTypeOperation.createCopy(setName, copySetName);

        // then
        Set<String> copySet = setTypeOperation.getAll(copySetName).get();
        assertThat(copySet, is(notNullValue()));
        assertThat(copySet.size(), is(tags.size()));
        tags.stream().forEach(tag -> {
            assertThat(copySet, hasItem(tag));
        });
    }

    @Test
    public void testDiff() {
        String setFrom = "numbers";
        String toSet = "numbers1";
        setTypeOperation.add(setFrom, "1", "2", "3");
        setTypeOperation.add(toSet, "5", "6", "7");

        // when
        Set<String> diff = setTypeOperation.diff(setFrom, toSet);

        // then
        assertThat(diff.size(), is(3));
        assertThat(diff, hasItem("1"));
        assertThat(diff, hasItem("2"));
        assertThat(diff, hasItem("3"));
    }
}