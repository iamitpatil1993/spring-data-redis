package com.example.spring.data.redis.type;

import com.example.spring.data.redis.BaseTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
public class HashTypeOperationTest extends BaseTest {

    @Autowired
    private HashTypeOperation<String> hashTypeOperation;

    @Test
    public void put() {
        // given
        String key = "user:".concat(UUID.randomUUID().toString());
        String hashKey = "username";
        String hashValue = UUID.randomUUID().toString();

        // when
        hashTypeOperation.put(key, hashKey, hashValue);

        // then
        assertThat(hashTypeOperation.size(key).get(), is(1l));
        Optional<String> result = hashTypeOperation.get(key, hashKey);
        assertThat(result.isPresent(), is(equalTo(true)));
        assertThat(result.get(), is(equalTo(hashValue)));
    }

    @Test
    public void get() {
        // given
        String key = "user:".concat(UUID.randomUUID().toString());
        String hashKey = "username";
        String hashValue = UUID.randomUUID().toString();
        hashTypeOperation.put(key, hashKey, hashValue);

        // when
        Optional<String> result = hashTypeOperation.get(key, hashKey);

        // then
        assertThat(result.isPresent(), is(equalTo(true)));
        assertThat(result.get(), is(equalTo(hashValue)));
    }


    @Test
    public void testPutMultiple() {
        // given
        String key = "user:".concat(UUID.randomUUID().toString());
        Map<String, String> hashValues = new HashMap<>(3);
        hashValues.put("username", UUID.randomUUID().toString());
        hashValues.put("firstName", UUID.randomUUID().toString());
        hashValues.put("lastName", UUID.randomUUID().toString());

        // when
        hashTypeOperation.putMultiple(key, hashValues);

        // then
        assertThat(hashTypeOperation.size(key).get(), is(equalTo(3l)));
    }

    @Test
    public void testRemove() {
        // given
        String key = "user:".concat(UUID.randomUUID().toString());
        String hashKey = "username";
        String hashValue = UUID.randomUUID().toString();
        hashTypeOperation.put(key, hashKey, hashValue);

        // when
        boolean isRemoved = hashTypeOperation.remove(key, hashKey);

        // then
        assertThat(isRemoved, is(true));
        assertThat(hashTypeOperation.get(key, hashKey).isPresent(), is(false));
    }

    @Test
    public void testGetAll() {
        // given
        String key = "user:".concat(UUID.randomUUID().toString());
        Map<String, String> hashValues = new HashMap<>(3);
        hashValues.put("username", UUID.randomUUID().toString());
        hashValues.put("firstName", UUID.randomUUID().toString());
        hashValues.put("lastName", UUID.randomUUID().toString());
        hashTypeOperation.putMultiple(key, hashValues);

        // when
        Optional<Map<String, String>> hash = hashTypeOperation.getAll(key);

        // then
        assertThat(hash.isPresent(), is(true));
        assertThat(hash.get().size(), is(equalTo(hashValues.size())));
    }

    @Test
    public void getAllUsingScan() throws IOException {
        for (int i = 0; i < 100000; i++) {
            hashTypeOperation.put("numbers", new Integer(i).toString(), new Integer(i).toString());
        }

        // when
        hashTypeOperation.getAllUsingScan("numbers");
    }
}