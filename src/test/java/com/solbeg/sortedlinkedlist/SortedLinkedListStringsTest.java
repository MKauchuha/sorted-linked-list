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

public class SortedLinkedListStringsTest {

    static final Comparator<String> NULLS_LAST_COMPARATOR = Comparator.nullsLast(String::compareTo);
    static final Comparator<String> NULLS_FIRST_COMPARATOR = Comparator.nullsFirst(String::compareTo);

    String[] strings;
    String[] stringsWithNulls;

    SortedLinkedList<String> sut;

    @BeforeEach
    void init() {
        sut = new SortedLinkedList<>();
        strings = new String[] {"mn", "cdddd", "ea", "cd", "e", "zzz", "ad", "c", "cba"};
        stringsWithNulls = new String[]  {"mn", null, "cdddd", "ea", "cd", "e", null, "zzz", "ad", "c", "cba"};
    }

    @Test
    void shouldAddInAscendingOrderNonNullsParams() {
        //GIVEN
        //WHEN
        Stream.of(strings).forEach(sut::add);

        //THEN
        assertFalse(sut.isEmpty());
        assertEquals(strings.length, sut.size());
        Arrays.sort(strings);
        assertEquals(Arrays.asList(strings), sut.toList());
    }

    @Test
    void shouldAddInAscendingOrder_TrailingNullsStrategy() {
        //GIVEN
        //WHEN
        Stream.of(stringsWithNulls).forEach(sut::add);

        //THEN
        assertFalse(sut.isEmpty());
        assertEquals(stringsWithNulls.length, sut.size());
        Arrays.sort(stringsWithNulls, NULLS_LAST_COMPARATOR);
        assertEquals(Arrays.asList(stringsWithNulls), sut.toList());
    }

    @Test
    void shouldAddInAscendingOrder_LeadingNullsStrategy() {
        //GIVEN
        sut = new SortedLinkedList<>(AddNullsStrategy.LEADING_NULLS);

        //WHEN
        Stream.of(stringsWithNulls).forEach(sut::add);

        //THEN
        assertFalse(sut.isEmpty());
        assertEquals(stringsWithNulls.length, sut.size());
        Arrays.sort(stringsWithNulls, NULLS_FIRST_COMPARATOR);
        assertEquals(Arrays.asList(stringsWithNulls), sut.toList());
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
        Stream.of(strings).forEach(sut::add);

        //THEN
        assertFalse(sut.isEmpty());
        assertThrows(IndexOutOfBoundsException.class, () -> sut.get(strings.length));
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
        Stream.of(strings).forEach(sut::add);
        sut.clear();

        //THEN
        assertTrue(sut.isEmpty());
        assertEquals(0, sut.size());
    }

    @Test
    void shouldReturnProperValue() {
        //GIVEN
        sut = new SortedLinkedList<>(AddNullsStrategy.LEADING_NULLS);

        //WHEN
        Stream.of(stringsWithNulls).forEach(sut::add);

        //THEN
        assertFalse(sut.isEmpty());
        assertEquals(stringsWithNulls.length, sut.size());

        Arrays.sort(stringsWithNulls, NULLS_FIRST_COMPARATOR);
        for (int i = 0; i < stringsWithNulls.length; i++) {
            assertEquals(stringsWithNulls[i], sut.get(i));
        }
    }

    @Test
    void shouldAddCollection() {
        //GIVEN
        sut = new SortedLinkedList<>(AddNullsStrategy.LEADING_NULLS);
        List<String> joinedList = new ArrayList<>(Arrays.asList(stringsWithNulls));
        joinedList.addAll(Arrays.asList(strings));
        joinedList.sort(NULLS_FIRST_COMPARATOR);

        //WHEN
        Stream.of(stringsWithNulls).forEach(sut::add);
        sut.addAll(Arrays.asList(strings));

        //THEN
        assertFalse(sut.isEmpty());
        assertEquals(stringsWithNulls.length + strings.length, sut.size());
        assertEquals(joinedList, sut.toList());
    }

    @Test
    void shouldAddAnotherLinkedList() {
        //GIVEN
        List<String> joinedList = new ArrayList<>(Arrays.asList(stringsWithNulls));
        joinedList.addAll(Arrays.asList(strings));
        joinedList.sort(NULLS_LAST_COMPARATOR);

        sut = new SortedLinkedList<>(Arrays.asList(stringsWithNulls));
        SortedLinkedList<String> secondList = new SortedLinkedList<>(Arrays.asList(strings));

        //WHEN
        sut.addAll(secondList);

        //THEN
        assertFalse(sut.isEmpty());
        assertEquals(stringsWithNulls.length + strings.length, sut.size());
        assertEquals(joinedList, sut.toList());

        sut.forEach(System.out::println);
    }

    @Test
    void shouldRemoveSecondElement() {
        //GIVEN
        int idx = 2;

        //WHEN
        sut.addAll(Arrays.asList(strings));
        String removedValue = sut.remove(idx);

        //THEN
        assertFalse(sut.isEmpty());
        assertEquals(strings.length - 1, sut.size());
        Arrays.sort(strings);
        assertEquals(strings[idx], removedValue);
    }

    @Test
    void shouldRemoveAllElement_OneByOne_StartingFromHead() {
        //GIVEN
        //WHEN
        sut.addAll(Arrays.asList(stringsWithNulls));
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
        sut.addAll(Arrays.asList(stringsWithNulls));
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
        sut.addAll(Arrays.asList(stringsWithNulls));
        for (int i = 0; i < stringsWithNulls.length; i++) {
            sut.remove(stringsWithNulls[i]);
        }

        //THEN
        assertTrue(sut.isEmpty());
        assertEquals(0, sut.size());
    }

    @Test
    void shouldNotRemoveValueWhenObjectIsNotPresent() {
        //GIVEN
        //WHEN
        sut.addAll(Arrays.asList(stringsWithNulls));
        String removedValue = sut.remove("Not present string");

        //THEN
        assertNull(removedValue);
        assertFalse(sut.isEmpty());
        assertEquals(stringsWithNulls.length, sut.size());
        Arrays.sort(stringsWithNulls, NULLS_LAST_COMPARATOR);
        assertEquals(Arrays.asList(stringsWithNulls), sut.toList());
    }
}
