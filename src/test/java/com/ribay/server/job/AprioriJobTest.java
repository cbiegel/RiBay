package com.ribay.server.job;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by CD on 09.08.2016.
 */
public class AprioriJobTest {

    @Test
    public void testGenerateSubsets() {
        Set<Integer> input = new HashSet<>(Arrays.asList(0, 1, 2, 3, 4));

        Set<Set<Integer>> actual = AprioriJob.getSubsetsWithSizeOfTwo(input);

        Set<Set<Integer>> expected = set(set(0, 1), set(0, 2), set(0, 3), set(0, 4), set(1, 2), set(1, 3), set(1, 4), set(2, 3), set(2, 4), set(3, 4));

        assertEquals(expected, actual);
    }

    @Test
    public void testCountOccurrences() {

        List<Set<Integer>> input = Arrays.asList(set(1, 2, 3), set(1, 2));

        Map<Set<Integer>, Long> actual = AprioriJob.countOccurrencesOfSubsetsWithSizeOfTwo(input);

        Map<Set<Integer>, Long> expected = new HashMap<>();
        expected.put(set(1, 2), 2L);
        expected.put(set(1, 3), 1L);
        expected.put(set(2, 3), 1L);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetOtherFrequentItems() {
        Map<Set<Integer>, Long> input = new HashMap<>();
        input.put(set(0, 1), 3L);
        input.put(set(1, 2), 2L);
        input.put(set(1, 3), 1L);
        input.put(set(2, 3), 1L);

        List<Integer> actual;
        List<Integer> expected;

        actual = AprioriJob.getMostOtherItemsInSet(1, input, 1);
        expected = Arrays.asList(0);
        assertEquals(expected, actual);

        actual = AprioriJob.getMostOtherItemsInSet(1, input, 2);
        expected = Arrays.asList(0, 2);
        assertEquals(expected, actual);

        actual = AprioriJob.getMostOtherItemsInSet(1, input, 4);
        expected = Arrays.asList(0, 2, 3);
        assertEquals(expected, actual);

        actual = AprioriJob.getMostOtherItemsInSet(1, input, 5);
        expected = Arrays.asList(0, 2, 3);
        assertEquals(expected, actual);

        actual = AprioriJob.getMostOtherItemsInSet(0, input, 1);
        expected = Arrays.asList(1);
        assertEquals(expected, actual);

        actual = AprioriJob.getMostOtherItemsInSet(2, input, 1);
        expected = Arrays.asList(1);
        assertEquals(expected, actual);

        actual = AprioriJob.getMostOtherItemsInSet(2, input, 2);
        expected = Arrays.asList(1,3);
        assertEquals(expected, actual);

        actual = AprioriJob.getMostOtherItemsInSet(4, input, 0);
        expected = Arrays.asList();
        assertEquals(expected, actual);
    }

    private <T> Set<T> set(T... items) {
        return new HashSet<>(Arrays.asList(items));
    }

    private Set<Integer> set(Integer... items) {
        return new HashSet<>(Arrays.asList(items));
    }

}
