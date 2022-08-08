package com.solbeg.sortedlinkedlist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class SortedLinkedList<T extends Comparable<T>> implements Iterable<T> {

    private static final String INDEX_OUT_OF_BOUND_EXCEPTION = "Index value %d current list size %d";

    private Comparator<T> comparator = new DefaultComparator<>(AddNullsStrategy.TRAILING_NULLS);
    private AddNullsStrategy addNullsStrategy = AddNullsStrategy.TRAILING_NULLS;

    private int size = 0;
    private Node<T> head;
    private Node<T> tail;

    public SortedLinkedList() {
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public SortedLinkedList(boolean reversed) {
        this.comparator = reversed
                ? new ReversedComparator<>(addNullsStrategy)
                : new DefaultComparator<>(addNullsStrategy);
    }

    public SortedLinkedList(AddNullsStrategy addNullsStrategy, boolean reversed) {
        this();
        this.addNullsStrategy = addNullsStrategy;
        this.comparator = reversed
                ? new ReversedComparator<>(addNullsStrategy)
                : new DefaultComparator<>(addNullsStrategy);
    }

    public SortedLinkedList(Collection<? extends T> collection) {
        this();
        addAll(collection);
    }

    public SortedLinkedList(AddNullsStrategy addNullsStrategy, Collection<? extends T> collection, boolean reversed) {
        this(addNullsStrategy, reversed);
        addAll(collection);
    }

    public SortedLinkedList(SortedLinkedList<? extends T> linkedList) {
        this();
        addAll(linkedList);
    }

    public SortedLinkedList(AddNullsStrategy addNullsStrategy, SortedLinkedList<? extends T> linkedList, boolean reversed) {
        this(addNullsStrategy, reversed);
        addAll(linkedList);
    }

    public int size() {
        return size;
    }

    public T get(int index) {
        return getNode(index).item;
    }

    public boolean addAll(Collection<? extends T> collection) {
        if (isNull(collection) || collection.isEmpty()) {
            return false;
        }
        collection.forEach(this::add);
        return true;
    }

    public boolean addAll(SortedLinkedList<? extends T> linkedList) {
        if (isEmpty()) {
            return false;
        }
        linkedList.forEach(this::add);
        return true;
    }

    public boolean add(T item) {
        if (isNull(item)) {
            addNullItem();
            size++;
            return true;
        }

        if (isEmpty()) {
            head = tail = new Node<>(item, null, null);
            size++;
            return true;
        }

        //item less than head
        if (comparator.compare(item, head.item) <= 0) {
            head = insertBefore(head, item);
            size++;
            return true;
        //item greater than tail
        } else if (comparator.compare(item, tail.item) > 0) {
            tail = insertAfter(tail, item);
            size++;
            return true;
        }

        Node<T> foundNode = findInsertNodePosition(item);
        insertBefore(foundNode, item);
        size++;
        return true;
    }

    public T remove(int index) {
        Node<T> node = getNode(index);
        T item = node.item;
        removeNode(node);
        return item;
    }

    public T remove(T item) {
        Node<T> node = getNode(item);
        if (isNull(node)) {
            return null;
        }
        T returnItem = node.item;
        removeNode(node);
        return returnItem;
    }

    public void clear() {
        if (isEmpty()) {
            return;
        }
        Node<T> node = head;
        while (nonNull(node)) {
            Node<T> next = node.next;
            clearNodeData(node);
            node = next;
        }
        head = tail = null;
        size = 0;
    }

    public List<T> toList() {
        if (isEmpty()) {
            return Collections.emptyList();
        }
        Iterator<T> iterator = iterator();
        ArrayList<T> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }

    private void clearNodeData(Node<T> node) {
        node.prev = null;
        node.next = null;
        node.item = null;
    }

    private void removeNode(Node<T> node) {
        Node<T> prevNode = node.prev;
        Node<T> nextNode = node.next;
        if (nonNull(prevNode)) {
            prevNode.next = node.next;
        } else {
            head = nextNode;
        }
        if (nonNull(nextNode)) {
            nextNode.prev = node.prev;
        } else {
            tail = prevNode;
        }
        clearNodeData(node);
        size--;
    }

    private Node<T> insertBefore(Node<T> node, T item) {
        Objects.requireNonNull(node);
        Node<T> prevNode = node.prev;
        Node<T> newNode = new Node<>(item, node, prevNode);
        node.prev = newNode;
        if (nonNull(prevNode)) {
            prevNode.next = newNode;
        }
        return newNode;
    }

    private Node<T> insertAfter(Node<T> node, T item) {
        Objects.requireNonNull(node);
        Node<T> nextNode = node.next;
        Node<T> newNode = new Node<>(item, nextNode, node);
        node.next = newNode;
        if (nonNull(nextNode)) {
            nextNode.prev = newNode;
        }
        return newNode;
    }

    private Node<T> getNode(int index) {
        checkIndex(index);
        boolean searchFromHead = index <= (size / 2);
        Node<T> node = searchFromHead ? head : tail;
        if (searchFromHead) {
            for (int i = 0; i < index; i++) {
                node = node.next;
            }
        } else {
            for (int i = size - 1; i > index; i--) {
                node = node.prev;
            }
        }
        return node;
    }

    private Node<T> getNode(T item) {
        if (isEmpty()) {
            return null;
        }
        Iterator<T> iterator = iterator();
        Node<T> node = null;
        boolean found = false;
        while (iterator.hasNext() && !found) {
            node = ((SortedListIterator) iterator).nextNode();
            found = comparator.compare(item, node.item) == 0;
        }
        return found ? node : null;
    }

    public Iterator<T> iterator() {
        return new SortedListIterator(0);
    }

    private Node<T> findInsertNodePosition(T item) {
        Objects.requireNonNull(item);
        Iterator<T> iterator = iterator();
        Node<T> node = null;
        boolean insertPositionFound = false;
        while (iterator.hasNext() && !insertPositionFound) {
            node = ((SortedListIterator) iterator).nextNode();
            insertPositionFound = comparator.compare(item, node.item) <= 0;
        }
        return node;
    }

    private void addNullItem() {
        if (isEmpty()) {
            head = tail = new Node<>(null, null, null);
            return;
        }
        if (addNullsStrategy == AddNullsStrategy.TRAILING_NULLS) {
            tail = insertAfter(tail, null);
        } else {
            head = insertBefore(head, null);
        }
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            String message = String.format(INDEX_OUT_OF_BOUND_EXCEPTION, index, size);
            throw new IndexOutOfBoundsException(message);
        }
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SortedLinkedList))
            return false;

        Iterator<T> thisIterator = iterator();
        Iterator<?> comparingIterator = ((SortedLinkedList<?>) o).iterator();
        while (thisIterator.hasNext() && comparingIterator.hasNext()) {
            T o1 = thisIterator.next();
            Object o2 = comparingIterator.next();
            if (!(isNull(o1) ? isNull(o2) : o1.equals(o2)))
                return false;
        }
        return !(thisIterator.hasNext() || comparingIterator.hasNext());
    }

    public int hashCode() {
        int hashCode = 1;
        for (T e : this)
            hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
        return hashCode;
    }

    public Spliterator<T> getSpliterator() {
        return new SortedListSpliterator<>(this, -1);
    }

    public Stream<T> stream() {
        return StreamSupport.stream(getSpliterator(), false);
    }

    private static final class Node<T> {
        private T item;
        private Node<T> next;
        private Node<T> prev;

        public Node(T item, Node<T> next, Node<T> prev) {
            this.item = item;
            this.next = next;
            this.prev = prev;
        }
    }

    private static class DefaultComparator<E extends Comparable<E>> implements Comparator<E> {

        private final AddNullsStrategy addNullsStrategy;

        public DefaultComparator(AddNullsStrategy addNullsStrategy) {
            this.addNullsStrategy = addNullsStrategy;
        }

        @Override
        public int compare(E o1, E o2) {
            if (o1 == o2)
                return 0;
            if (o1 == null)
                return addNullsStrategy == AddNullsStrategy.LEADING_NULLS ? 1 : -1;
            if (o2 == null)
                return addNullsStrategy == AddNullsStrategy.TRAILING_NULLS ? -1 : 1;
            return o1.compareTo(o2);
        }
    }

    private static class ReversedComparator<E extends Comparable<E>> implements Comparator<E> {

        private final AddNullsStrategy addNullsStrategy;

        public ReversedComparator(AddNullsStrategy addNullsStrategy) {
            this.addNullsStrategy = addNullsStrategy;
        }

        @Override
        public int compare(E o1, E o2) {
            if (o1 == o2)
                return 0;
            if (o1 == null)
                return addNullsStrategy == AddNullsStrategy.LEADING_NULLS ? 1 : -1;
            if (o2 == null)
                return addNullsStrategy == AddNullsStrategy.TRAILING_NULLS ? -1 : 1;
            return o2.compareTo(o1);
        }
    }

    private class SortedListIterator implements Iterator<T> {
        private SortedLinkedList.Node<T> next;
        private int nextIndex;

        SortedListIterator(int index) {
            checkIndex(index);
            next = getNode(index);
            nextIndex = index;
        }

        public boolean hasNext() {
            return nextIndex < size;
        }

        public T next() {
            if (!hasNext())
                throw new NoSuchElementException();

            SortedLinkedList.Node<T> last = next;
            next = next.next;
            nextIndex++;
            return last.item;
        }

        SortedLinkedList.Node<T> nextNode() {
            if (!hasNext())
                throw new NoSuchElementException();

            SortedLinkedList.Node<T> last = next;
            next = next.next;
            nextIndex++;
            return last;
        }
    }

    static class SortedListSpliterator<E extends Comparable<E>> implements Spliterator<E> {

        private static final int BATCH_SIZE = 1024 * 1024;
        private final SortedLinkedList<E> list;
        private Node<E> currentNode;
        private int estSize;

        public SortedListSpliterator(SortedLinkedList<E> list, int estSize) {
            this.list = list;
            this.estSize = estSize;
        }

        @Override
        public boolean tryAdvance(Consumer<? super E> action) {
            if (action == null)
                throw new NullPointerException();
            if (nonNull(currentNode)) {
                --estSize;
                E e = currentNode.item;
                currentNode = currentNode.next;
                action.accept(e);
                return true;
            }
            return false;
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            if (action == null) throw new NullPointerException();
            if (estSize > 0 && currentNode != null) {
                do {
                    E e = currentNode.item;
                    currentNode = currentNode.next;
                    action.accept(e);
                } while (currentNode != null && estSize > 0);
                estSize = 0;
                currentNode = null;
            }
        }

        @Override
        public Spliterator<E> trySplit() {
            if (estSize > 1 && nonNull(currentNode)) {
                int n = Math.min(BATCH_SIZE, estSize);
                Object[] a = new Object[n];
                for (int i = 0; i < n && nonNull(currentNode); i++) {
                    a[i] = currentNode.item;
                    currentNode = currentNode.next;
                    estSize--;
                }
                return Spliterators.spliterator(a, 0, n, Spliterator.ORDERED);
            }
            return null;
        }

        @Override
        public long estimateSize() {
            int s;
            SortedLinkedList<E> lst;
            if ((s = estSize) < 0) {
                if ((lst = list) == null)
                    s = estSize = 0;
                else {
                    currentNode = lst.head;
                    s = estSize = lst.size;
                }
            }
            return s;
        }

        @Override
        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.SORTED;
        }

        @Override
        public Comparator<? super E> getComparator() {
            return list.comparator;
        }
    }
}
