package org.howard.edu.lsp.assignment5;

import java.util.Collections;
import java.util.TreeSet;

/**
 * A mutable mathematical set of integers with no duplicate values.
 * Elements are displayed in ascending numerical order. Operations returning
 * an {@code IntegerSet} create an independent result and leave both operands
 * unchanged. Set arguments must be non-null, except that {@link #equals(IntegerSet)}
 * returns false for null.
 */
public class IntegerSet {
    private final TreeSet<Integer> elements = new TreeSet<>();

    /** Creates an empty set. */
    public IntegerSet() {
    }

    /** Removes all elements from this set. */
    public void clear() {
        elements.clear();
    }

    /**
     * Returns the number of distinct elements.
     *
     * @return this set's size
     */
    public int length() {
        return elements.size();
    }

    /**
     * Compares the elements of two sets, regardless of insertion order.
     *
     * @param b the set to compare, or null
     * @return true if b is non-null and contains exactly the same elements
     */
    public boolean equals(IntegerSet b) {
        return b != null && elements.equals(b.elements);
    }

    /**
     * Tests whether a value belongs to this set.
     *
     * @param value the value to find
     * @return true if the value is present
     */
    public boolean contains(int value) {
        return elements.contains(value);
    }

    /**
     * Returns the largest element.
     *
     * @return the greatest integer in this set
     * @throws IllegalStateException if this set is empty
     */
    public int largest() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot find the largest element of an empty set.");
        }
        return elements.last();
    }

    /**
     * Returns the smallest element.
     *
     * @return the least integer in this set
     * @throws IllegalStateException if this set is empty
     */
    public int smallest() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot find the smallest element of an empty set.");
        }
        return elements.first();
    }

    /**
     * Adds an integer, ignoring it if already present.
     *
     * @param item the integer to add
     */
    public void add(int item) {
        elements.add(item);
    }

    /**
     * Removes an integer if present; otherwise leaves this set unchanged.
     *
     * @param item the integer to remove
     */
    public void remove(int item) {
        elements.remove(item);
    }

    /**
     * Returns the elements present in either set.
     *
     * @param b the other set
     * @return a new, independent union
     * @throws NullPointerException if b is null
     */
    public IntegerSet union(IntegerSet b) {
        IntegerSet result = new IntegerSet();
        result.elements.addAll(elements);
        result.elements.addAll(b.elements);
        return result;
    }

    /**
     * Returns the elements shared by both sets.
     *
     * @param b the other set
     * @return a new, independent intersection
     * @throws NullPointerException if b is null
     */
    public IntegerSet intersect(IntegerSet b) {
        IntegerSet result = new IntegerSet();
        result.elements.addAll(elements);
        result.elements.retainAll(b.elements);
        return result;
    }

    /**
     * Returns the elements in this set that are absent from b.
     *
     * @param b the set whose elements are excluded
     * @return a new, independent difference (this set minus b)
     * @throws NullPointerException if b is null
     */
    public IntegerSet diff(IntegerSet b) {
        IntegerSet result = new IntegerSet();
        result.elements.addAll(elements);
        result.elements.removeAll(b.elements);
        return result;
    }

    /**
     * Returns the elements in b that are absent from this set, following the
     * assignment's definition of complement.
     *
     * @param b the set from which this set's elements are excluded
     * @return a new, independent difference (b minus this set)
     * @throws NullPointerException if b is null
     */
    public IntegerSet complement(IntegerSet b) {
        return b.diff(this);
    }

    /**
     * Tests whether every element of this set is in b.
     * The empty set is a subset of every set.
     *
     * @param b the potential superset
     * @return true if b contains every element of this set
     * @throws NullPointerException if b is null
     */
    public boolean isSubsetOf(IntegerSet b) {
        return b.elements.containsAll(elements);
    }

    /**
     * Tests whether the sets have no elements in common.
     * An empty set is disjoint from every set.
     *
     * @param b the other set
     * @return true if the intersection is empty
     * @throws NullPointerException if b is null
     */
    public boolean isDisjoint(IntegerSet b) {
        return Collections.disjoint(elements, b.elements);
    }

    /**
     * Returns the elements that belong to exactly one of the two sets.
     *
     * @param b the other set
     * @return a new, independent symmetric difference
     * @throws NullPointerException if b is null
     */
    public IntegerSet symmetricDiff(IntegerSet b) {
        return diff(b).union(b.diff(this));
    }

    /**
     * Tests whether this set contains no elements.
     *
     * @return true if this set is empty
     */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /**
     * Formats the elements in ascending numerical order, inside square brackets
     * and separated by a comma and one space. An empty set is formatted as [].
     *
     * @return the formatted set
     */
    @Override
    public String toString() {
        return elements.toString();
    }
}
