package com.example.spring.data.redis.type;

import com.example.spring.data.redis.BaseTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author amit
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class SortedSetTypeOperationTest extends BaseTest {

    @Autowired
    private SortedSetTypeOperation<String> sortedSetTypeOperation;

    @Test
    public void add() {
        // given
        String setName = "names";

        // when
        sortedSetTypeOperation.add(setName, Math.random(), UUID.randomUUID().toString());
        sortedSetTypeOperation.add(setName, new DefaultTypedTuple(UUID.randomUUID().toString(), Math.random()));
        sortedSetTypeOperation.add(setName, new DefaultTypedTuple(UUID.randomUUID().toString(), Math.random() * 100));

        // then
        assertThat(sortedSetTypeOperation.size(setName).get(), is(3l));
        assertThat(sortedSetTypeOperation.countByScoreRange(setName, 0.0, 1.0).getAsLong(), is(2l));
    }


    @Test
    public void testIncrementScore() {
        // given
        final String setName = "names";
        Double initialScore = Math.random();
        String value = UUID.randomUUID().toString();
        sortedSetTypeOperation.add(setName, initialScore, value);

        // when
        Double newScore = sortedSetTypeOperation.incrementScore(setName, value, 100);

        // then
        assertThat(sortedSetTypeOperation.getScore(setName, value).get(), is(equalTo(newScore)));
    }

    @Test
    public void testRange() {
        final String setName = "names";
        sortedSetTypeOperation.add(setName, 1993, UUID.randomUUID().toString());
        sortedSetTypeOperation.add(setName, 1990, UUID.randomUUID().toString());
        sortedSetTypeOperation.add(setName, 1985, UUID.randomUUID().toString());

        // when
        Optional<Set<ZSetOperations.TypedTuple<String>>> range = sortedSetTypeOperation.range(setName, 1, 2);

        // then
        assertThat(range.isPresent(), is(true));
        assertThat(range.get().size(), is(2));
    }

    @Test
    public void testRemove() {
        final String setName = "names";
        final String value = UUID.randomUUID().toString();
        sortedSetTypeOperation.add(setName, 1993, value);
        sortedSetTypeOperation.add(setName, 1995, UUID.randomUUID().toString());

        // when
        Long removedCount = sortedSetTypeOperation.remove(setName, value);

        // then
        assertThat(removedCount, is(1l));
        assertThat(sortedSetTypeOperation.size(setName).get(), is(1l));
    }
}