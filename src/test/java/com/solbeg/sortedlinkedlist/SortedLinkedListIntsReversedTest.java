package com.solbeg.sortedlinkedlist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SortedLinkedListIntsReversedTest {

    static final Comparator<Integer> NULLS_LAST_COMPARATOR = Comparator.nullsFirst(Integer::compareTo).reversed();
    static final Comparator<Integer> NULLS_FIRST_COMPARATOR = Comparator.nullsLast(Integer::compareTo).reversed();
    //GIVEN
    Integer[] ints;
    Integer[] intsWithNulls;

    SortedLinkedList<Integer> sut;

    @BeforeEach
    void init() {
        sut = new SortedLinkedList<>(true);
        ints = new Integer[] {100, 4, 25, 17, 150, 11};
        intsWithNulls = new Integer[] {100, null, 4, 25, 17, null, 150, 11};
    }

    @Test
    void shouldAddInDescendingOrderNonNullsParams() {
        //GIVEN
        //WHEN
        Stream.of(ints).forEach(sut::add);

        //THEN
        assertFalse(sut.isEmpty());
        assertEquals(ints.length, sut.size());
        Arrays.sort(ints, NULLS_LAST_COMPARATOR);
        assertEquals(Arrays.asList(ints), sut.toList());
    }

    @Test
    void shouldAddInAscendingOrder_TrailingNullsStrategy() {
        //GIVEN
        //WHEN
        Stream.of(intsWithNulls).forEach(sut::add);

        //THEN
        assertFalse(sut.isEmpty());
        assertEquals(intsWithNulls.length, sut.size());
        Arrays.sort(intsWithNulls, NULLS_LAST_COMPARATOR);
        assertEquals(Arrays.asList(intsWithNulls), sut.toList());
    }

    @Test
    void shouldAddInAscendingOrder_LeadingNullsStrategy() {
        //GIVEN
        sut = new SortedLinkedList<>(AddNullsStrategy.LEADING_NULLS, true);

        //WHEN
        Stream.of(intsWithNulls).forEach(sut::add);

        //THEN
        assertFalse(sut.isEmpty());
        assertEquals(intsWithNulls.length, sut.size());
        Arrays.sort(intsWithNulls, NULLS_FIRST_COMPARATOR);
        assertEquals(Arrays.asList(intsWithNulls), sut.toList());
    }

    @Test
    void shouldRiseIndexOutOfBoundExceptionForEmptyList() {
        //GIVEN
        //WHEN
        //THEN
        assertTrue(sut.isEmpty());
        assertThrows(IndexOutOfBoundsException.class, () -> sut.get(0));
    }

    @Test
    void shouldRiseIndexOutOfBoundException() {
        //GIVEN
        //WHEN
        Stream.of(ints).forEach(sut::add);

        //THEN
        assertFalse(sut.isEmpty());
        assertThrows(IndexOutOfBoundsException.class, () -> sut.get(ints.length));
        assertThrows(IndexOutOfBoundsException.class, () -> sut.get(-1));
    }

    @Test
    void shouldHaveEmptyList() {
        //GIVEN
        //WHEN
        //THEN
        assertTrue(sut.isEmpty());
        assertEquals(0, sut.size());
    }

    @Test
    void shouldClearList() {
        //GIVEN
        //WHEN
        Stream.of(ints).forEach(sut::add);
        sut.clear();

        //THEN
        assertTrue(sut.isEmpty());
        assertEquals(0, sut.size());
    }

    @Test
    void shouldReturnProperValue() {
        //GIVEN
        sut = new SortedLinkedList<>(AddNullsStrategy.LEADING_NULLS, true);

        //WHEN
        Stream.of(intsWithNulls).forEach(sut::add);

        //THEN
        assertFalse(sut.isEmpty());
        assertEquals(intsWithNulls.length, sut.size());

        Arrays.sort(intsWithNulls, NULLS_FIRST_COMPARATOR);
        for (int i = 0; i < intsWithNulls.length; i++) {
            assertEquals(intsWithNulls[i], sut.get(i));
        }
    }

    @Test
    void shouldAddCollection() {
        //GIVEN
        sut = new SortedLinkedList<>(AddNullsStrategy.LEADING_NULLS, true);
        List<Integer> joinedList = new ArrayList<>(Arrays.asList(intsWithNulls));
        joinedList.addAll(Arrays.asList(ints));
        joinedList.sort(NULLS_FIRST_COMPARATOR);

        //WHEN
        Stream.of(intsWithNulls).forEach(sut::add);
        sut.addAll(Arrays.asList(ints));

        //THEN
        assertFalse(sut.isEmpty());
        assertEquals(intsWithNulls.length + ints.length, sut.size());
        assertEquals(joinedList, sut.toList());
    }

    @Test
    void shouldAddAnotherLinkedList() {
        //GIVEN
        List<Integer> joinedList = new ArrayList<>(Arrays.asList(intsWithNulls));
        joinedList.addAll(Arrays.asList(ints));
        joinedList.sort(NULLS_LAST_COMPARATOR);

        sut = new SortedLinkedList<>(AddNullsStrategy.TRAILING_NULLS, Arrays.asList(intsWithNulls), true);
        SortedLinkedList<Integer> secondList = new SortedLinkedList<>(Arrays.asList(ints));

        //WHEN
        sut.addAll(secondList);

        //THEN
        assertFalse(sut.isEmpty());
        assertEquals(intsWithNulls.length + ints.length, sut.size());
        assertEquals(joinedList, sut.toList());
    }

    @Test
    void shouldRemoveSecondElement() {
        //GIVEN
        int idx = 2;

        //WHEN
        sut.addAll(Arrays.asList(ints));
        Integer removedValue = sut.remove(idx);

        //THEN
        assertFalse(sut.isEmpty());
        assertEquals(ints.length - 1, sut.size());
        Arrays.sort(ints, NULLS_LAST_COMPARATOR);
        assertEquals(ints[idx], removedValue);
    }

    @Test
    void shouldRemoveAllElement_OneByOne_StartingFromHead() {
        //GIVEN
        //WHEN
        sut.addAll(Arrays.asList(intsWithNulls));
        while (!sut.isEmpty()) {
            sut.remove(0);
        }

        //THEN
        assertTrue(sut.isEmpty());
        assertEquals(0, sut.size());
    }

    @Test
    void shouldRemoveAllElement_OneByOne_StartingFromTail() {
        //GIVEN
        //WHEN
        sut.addAll(Arrays.asList(intsWithNulls));
        while (!sut.isEmpty()) {
            sut.remove(sut.size() - 1);
        }

        //THEN
        assertTrue(sut.isEmpty());
        assertEquals(0, sut.size());
    }

    @Test
    void shouldRemoveAllElement_PresentsInCollection() {
        //GIVEN
        //WHEN
        sut.addAll(Arrays.asList(intsWithNulls));
        for (int i = 0; i < intsWithNulls.length; i++) {
            sut.remove(intsWithNulls[i]);
        }

        //THEN
        assertTrue(sut.isEmpty());
        assertEquals(0, sut.size());
    }

    @Test
    void shouldNotRemoveValueWhenObjectIsNotPresent() {
        //GIVEN
        //WHEN
        sut.addAll(Arrays.asList(intsWithNulls));
        Integer removedValue = sut.remove(Integer.valueOf(1024));

        //THEN
        assertNull(removedValue);
        assertFalse(sut.isEmpty());
        assertEquals(intsWithNulls.length, sut.size());
        Arrays.sort(intsWithNulls, NULLS_LAST_COMPARATOR);
        assertEquals(Arrays.asList(intsWithNulls), sut.toList());
    }

    @Test
    void shouldAddNullToTheTail() {
        //GIVEN
        //WHEN
        sut.add(null);
        sut.add(null);
        sut.add(1024);

        //THEN
        assertFalse(sut.isEmpty());
        assertEquals(3, sut.size());
        assertEquals(1024, sut.get(0));
        assertNull(sut.get(1));
        assertNull(sut.get(2));
    }

    @Test
    void shouldAddNullsToTheHead() {
        //GIVEN
        sut = new SortedLinkedList<>(AddNullsStrategy.LEADING_NULLS, true);

        //WHEN
        sut.add(null);
        sut.add(null);
        sut.add(1024);

        //THEN
        assertFalse(sut.isEmpty());
        assertEquals(3, sut.size());
        assertNull(sut.get(0));
        assertNull(sut.get(1));
        assertEquals(1024, sut.get(2));
    }
}
