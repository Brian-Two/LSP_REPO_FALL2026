package org.howard.edu.lsp.assignment6;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Assignment 6: JUnit 5 tests for IntegerSet.
 *
 * Complete every TODO. Do not remove, rename, or disable test methods.
 * Each test must contain meaningful assertions and run independently.
 */
public class IntegerSetTest {

    // ===== clear() =====

    /**
     * Create a populated set; call clear(); assert that it is empty and its length is zero.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testClearClearsPopulatedSet() {
        IntegerSet s = set(-3, 0, 8);
        s.clear();
        assertTrue(s.isEmpty());
        assertEquals(0, s.length());
        assertFalse(s.contains(-3));
        assertFalse(s.contains(0));
        assertFalse(s.contains(8));
    }

    /**
     * Clear an already empty set; verify it remains empty.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testClearClearsEmptySet() {
        IntegerSet s = new IntegerSet();
        s.clear();
        assertTrue(s.isEmpty());
        assertEquals(0, s.length());
        assertEquals("[]", s.toString());
    }

    // ===== length() =====

    /**
     * Add repeated and unique integers; assert the number of distinct elements.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testLengthCountsUniqueElements() {
        IntegerSet s = set(3, 3, -2, 0, -2, 3);
        assertEquals(3, s.length());
    }

    /**
     * Verify a new empty set has length zero.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testLengthEmptySetLength() {
        IntegerSet s = new IntegerSet();
        assertEquals(0, s.length());
    }

    // ===== equals() =====

    /**
     * Construct sets with identical elements in different insertion orders; assert equality.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testEqualsIgnoresInsertionOrder() {
        IntegerSet a = set(-3, 0, 8);
        IntegerSet b = set(8, -3, 0);
        // Call the required equals(IntegerSet) overload directly.
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
        assertContents(a, -3, 0, 8);
        assertContents(b, -3, 0, 8);
    }

    /**
     * Construct sets of equal length with different elements; assert inequality.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testEqualsDetectsDifferentContents() {
        IntegerSet a = set(1, 2);
        IntegerSet b = set(1, 3);
        assertEquals(a.length(), b.length());
        assertFalse(a.equals(b));
        assertFalse(b.equals(a));
    }

    /**
     * Verify two separately constructed empty sets compare equal.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testEqualsEmptySetsEqual() {
        IntegerSet a = new IntegerSet();
        IntegerSet b = new IntegerSet();
        assertNotSame(a, b);
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
    }

    /**
     * Verify a set equals itself and differs from a set of another size.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testEqualsSameObjectAndDifferentSizes() {
        IntegerSet a = set(-1, 0);
        IntegerSet b = set(-1);
        assertTrue(a.equals(a));
        assertFalse(a.equals(b));
        assertFalse(b.equals(a));
    }

    // ===== contains() =====

    /**
     * Verify contains() is true for a present element and false for an absent one.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testContainsPresentAndAbsent() {
        IntegerSet s = set(-3, 0, 9);
        assertTrue(s.contains(-3));
        assertTrue(s.contains(0));
        assertTrue(s.contains(9));
        assertFalse(s.contains(8));
        assertFalse(s.contains(-8));
    }

    /**
     * Verify an empty set does not contain an arbitrary integer.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testContainsEmptySetContainsNothing() {
        IntegerSet s = new IntegerSet();
        assertFalse(s.contains(0));
        assertFalse(s.contains(-1));
        assertFalse(s.contains(Integer.MAX_VALUE));
    }

    // ===== largest() =====

    /**
     * Verify largest() returns the maximum across negative, zero, and positive values.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testLargestNegativeAndPositiveValues() {
        IntegerSet s = set(-20, 13, 0, -5);
        assertEquals(13, s.largest());
        assertEquals(-5, set(-20, -5, -10).largest());
        assertEquals(Integer.MAX_VALUE, set(Integer.MIN_VALUE, 0, Integer.MAX_VALUE).largest());
        assertEquals(-7, set(-7).largest());
        assertContents(s, -20, -5, 0, 13);
    }

    /**
     * Use assertThrows() to verify IllegalStateException for an empty set.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testLargestEmptySetThrowsException() {
        IntegerSet s = new IntegerSet();
        assertThrows(IllegalStateException.class, () -> s.largest());
    }

    // ===== smallest() =====

    /**
     * Verify smallest() returns the minimum across negative, zero, and positive values.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testSmallestNegativeAndPositiveValues() {
        IntegerSet s = set(20, -5, 0, 13);
        assertEquals(-5, s.smallest());
        assertEquals(5, set(20, 5, 10).smallest());
        assertEquals(Integer.MIN_VALUE, set(Integer.MAX_VALUE, 0, Integer.MIN_VALUE).smallest());
        assertEquals(7, set(7).smallest());
        assertContents(s, -5, 0, 13, 20);
    }

    /**
     * Use assertThrows() to verify IllegalStateException for an empty set.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testSmallestEmptySetThrowsException() {
        IntegerSet s = new IntegerSet();
        assertThrows(IllegalStateException.class, () -> s.smallest());
    }

    // ===== add() =====

    /**
     * Add the same integer repeatedly; verify only one copy exists.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testAddIgnoresDuplicates() {
        IntegerSet s = new IntegerSet();
        s.add(4);
        s.add(4);
        s.add(4);
        assertEquals(1, s.length());
        assertTrue(s.contains(4));
        assertEquals("[4]", s.toString());
    }

    /**
     * Add Integer.MIN_VALUE, Integer.MAX_VALUE, and zero; verify all are represented.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testAddHandlesIntegerBoundaries() {
        IntegerSet s = new IntegerSet();
        s.add(Integer.MAX_VALUE);
        s.add(Integer.MIN_VALUE);
        s.add(0);
        assertContents(s, Integer.MIN_VALUE, 0, Integer.MAX_VALUE);
        assertEquals("[-2147483648, 0, 2147483647]", s.toString());
    }

    // ===== remove() =====

    /**
     * Remove a present element; verify it is absent and others remain.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testRemoveRemovesExistingElement() {
        IntegerSet s = set(1, 2, 3);
        s.remove(2);
        assertFalse(s.contains(2));
        assertContents(s, 1, 3);
        s.remove(1);
        s.remove(3);
        assertTrue(s.isEmpty());
        assertEquals(0, s.length());
    }

    /**
     * Remove an absent element; verify the set is unchanged.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testRemoveIgnoresAbsentElement() {
        IntegerSet s = set(-1, 0, 3);
        s.remove(9);
        assertContents(s, -1, 0, 3);
        IntegerSet empty = new IntegerSet();
        empty.remove(9);
        assertContents(empty);
    }

    // ===== union() =====

    /**
     * Verify union() contains all distinct elements from two overlapping sets.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testUnionOverlappingSets() {
        IntegerSet a = set(-3, 0, 8);
        IntegerSet b = set(8, 11);
        IntegerSet result = a.union(b);
        assertResult(result, a, b, -3, 0, 8, 11);
        assertContents(a, -3, 0, 8);
        assertContents(b, 8, 11);
    }

    /**
     * Verify union with an empty set and with the same object.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testUnionEmptyAndSameOperand() {
        IntegerSet a = set(1, 2);
        IntegerSet empty = new IntegerSet();
        assertResult(a.union(empty), a, empty, 1, 2);
        assertResult(empty.union(a), empty, a, 1, 2);
        IntegerSet self = a.union(a);
        assertResult(self, a, a, 1, 2);
        self.remove(1);
        assertContents(a, 1, 2);
        assertResult(empty.union(empty), empty, empty);
        assertContents(empty);
    }

    /**
     * Verify both inputs remain unchanged; modify the result and verify the inputs are still unchanged.
     * Assignment 5 equivalent: 3 point(s).
     */
    @Test
    public void testUnionDoesNotMutateInputs() {
        IntegerSet a = set(1, 2);
        IntegerSet b = set(2, 3);
        IntegerSet result = a.union(b);
        assertResult(result, a, b, 1, 2, 3);
        assertContents(a, 1, 2);
        assertContents(b, 2, 3);

        result.remove(1);
        result.add(99);
        assertContents(result, 2, 3, 99);
        assertContents(a, 1, 2);
        assertContents(b, 2, 3);

        a.clear();
        b.add(77);
        assertContents(result, 2, 3, 99);
        assertContents(a);
        assertContents(b, 2, 3, 77);
    }

    // ===== intersect() =====

    /**
     * Verify intersection contains exactly the shared elements.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testIntersectOverlappingSets() {
        IntegerSet a = set(-3, 0, 8);
        IntegerSet b = set(8, -3, 11);
        IntegerSet result = a.intersect(b);
        assertResult(result, a, b, -3, 8);
        assertContents(a, -3, 0, 8);
        assertContents(b, -3, 8, 11);
    }

    /**
     * Verify disjoint intersection is empty and self-intersection is unchanged.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testIntersectDisjointAndSameOperand() {
        IntegerSet a = set(1, 2);
        IntegerSet b = set(3, 4);
        IntegerSet empty = new IntegerSet();
        assertResult(a.intersect(b), a, b);
        assertResult(a.intersect(empty), a, empty);
        assertResult(empty.intersect(a), empty, a);
        IntegerSet self = a.intersect(a);
        assertResult(self, a, a, 1, 2);
        self.remove(1);
        assertContents(a, 1, 2);
        assertContents(b, 3, 4);
        assertContents(empty);
    }

    /**
     * Modify the intersection result and verify both input sets remain unchanged.
     * Assignment 5 equivalent: 3 point(s).
     */
    @Test
    public void testIntersectIndependentResult() {
        IntegerSet a = set(1, 2);
        IntegerSet b = set(2, 3);
        IntegerSet result = a.intersect(b);
        assertResult(result, a, b, 2);
        assertContents(a, 1, 2);
        assertContents(b, 2, 3);

        result.remove(2);
        result.add(99);
        assertContents(result, 99);
        assertContents(a, 1, 2);
        assertContents(b, 2, 3);

        a.clear();
        b.add(77);
        assertContents(result, 99);
        assertContents(a);
        assertContents(b, 2, 3, 77);
    }

    // ===== diff() =====

    /**
     * Verify A.diff(B) returns elements in A but not B, and distinguish direction.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testDiffDirectionMatters() {
        IntegerSet a = set(1, 2, 3);
        IntegerSet b = set(2, 3, 4);
        assertResult(a.diff(b), a, b, 1);
        assertResult(b.diff(a), b, a, 4);
        assertContents(a, 1, 2, 3);
        assertContents(b, 2, 3, 4);
    }

    /**
     * Verify difference with an empty set and difference with itself.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testDiffEmptyAndSameOperand() {
        IntegerSet a = set(1, 2);
        IntegerSet empty = new IntegerSet();
        assertResult(a.diff(empty), a, empty, 1, 2);
        assertResult(empty.diff(a), empty, a);
        assertResult(a.diff(a), a, a);
        assertResult(empty.diff(empty), empty, empty);
        assertContents(a, 1, 2);
        assertContents(empty);
    }

    /**
     * Modify the difference result and verify neither input changes.
     * Assignment 5 equivalent: 3 point(s).
     */
    @Test
    public void testDiffIndependentResult() {
        IntegerSet a = set(1, 2);
        IntegerSet b = set(2, 3);
        IntegerSet result = a.diff(b);
        assertResult(result, a, b, 1);
        assertContents(a, 1, 2);
        assertContents(b, 2, 3);

        result.remove(1);
        result.add(99);
        assertContents(result, 99);
        assertContents(a, 1, 2);
        assertContents(b, 2, 3);

        a.clear();
        b.add(77);
        assertContents(result, 99);
        assertContents(a);
        assertContents(b, 2, 3, 77);
    }

    // ===== complement() =====

    /**
     * Verify A.complement(B) returns elements in B but not A.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testComplementReverseDifference() {
        IntegerSet a = set(1, 2, 3);
        IntegerSet b = set(2, 3, 4);
        assertResult(a.complement(b), a, b, 4);
        assertResult(b.complement(a), b, a, 1);
        assertResult(a.complement(a), a, a);
        assertContents(a, 1, 2, 3);
        assertContents(b, 2, 3, 4);
    }

    /**
     * Verify complement with an empty receiver and with an empty argument.
     * Assignment 5 equivalent: 1 point(s).
     */
    @Test
    public void testComplementEmptyOperands() {
        IntegerSet a = set(5);
        IntegerSet empty = new IntegerSet();
        assertResult(empty.complement(a), empty, a, 5);
        assertResult(a.complement(empty), a, empty);
        assertResult(empty.complement(empty), empty, empty);
        assertContents(a, 5);
        assertContents(empty);
    }

    /**
     * Modify the complement result and verify neither input changes.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testComplementIndependentResult() {
        IntegerSet a = set(1, 2);
        IntegerSet b = set(2, 3);
        IntegerSet result = a.complement(b);
        assertResult(result, a, b, 3);
        assertContents(a, 1, 2);
        assertContents(b, 2, 3);

        result.remove(3);
        result.add(99);
        assertContents(result, 99);
        assertContents(a, 1, 2);
        assertContents(b, 2, 3);

        a.clear();
        b.add(77);
        assertContents(result, 99);
        assertContents(a);
        assertContents(b, 2, 3, 77);
    }

    // ===== isSubsetOf() =====

    /**
     * Verify a proper subset is recognized.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testIsSubsetOfProperSubset() {
        IntegerSet smaller = set(1, 2);
        IntegerSet larger = set(1, 2, 3);
        assertTrue(smaller.isSubsetOf(larger));
        assertFalse(larger.isSubsetOf(smaller));
        assertContents(smaller, 1, 2);
        assertContents(larger, 1, 2, 3);
    }

    /**
     * Verify a set with an element missing from the other set is not a subset.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testIsSubsetOfNotSubset() {
        IntegerSet a = set(1, 4);
        IntegerSet b = set(1, 2, 3);
        assertFalse(a.isSubsetOf(b));
        assertFalse(b.isSubsetOf(a));
        assertContents(a, 1, 4);
        assertContents(b, 1, 2, 3);
    }

    /**
     * Verify equal sets are subsets of each other.
     * Assignment 5 equivalent: 1 point(s).
     */
    @Test
    public void testIsSubsetOfEqualSets() {
        IntegerSet a = set(2, 1);
        IntegerSet b = set(1, 2);
        assertTrue(a.isSubsetOf(b));
        assertTrue(b.isSubsetOf(a));
        assertTrue(a.isSubsetOf(a));
    }

    /**
     * Verify the empty set is a subset of a nonempty set, but not vice versa.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testIsSubsetOfEmptyCases() {
        IntegerSet empty = new IntegerSet();
        IntegerSet nonempty = set(1);
        assertTrue(empty.isSubsetOf(nonempty));
        assertFalse(nonempty.isSubsetOf(empty));
        assertTrue(empty.isSubsetOf(new IntegerSet()));
    }

    // ===== isDisjoint() =====

    /**
     * Verify disjoint sets return true.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testIsDisjointNoOverlap() {
        IntegerSet a = set(1, 2);
        IntegerSet b = set(3, 4);
        assertTrue(a.isDisjoint(b));
        assertTrue(b.isDisjoint(a));
        assertContents(a, 1, 2);
        assertContents(b, 3, 4);
    }

    /**
     * Verify overlapping sets return false.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testIsDisjointOverlap() {
        IntegerSet a = set(1, 2);
        IntegerSet b = set(2, 4);
        assertFalse(a.isDisjoint(b));
        assertFalse(b.isDisjoint(a));
        assertContents(a, 1, 2);
        assertContents(b, 2, 4);
    }

    /**
     * Verify disjointness with an empty set and with the same nonempty object.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testIsDisjointEmptyAndSameOperand() {
        IntegerSet empty = new IntegerSet();
        IntegerSet nonempty = set(1, 2);
        assertTrue(empty.isDisjoint(nonempty));
        assertTrue(nonempty.isDisjoint(empty));
        assertTrue(empty.isDisjoint(empty));
        assertFalse(nonempty.isDisjoint(nonempty));
    }

    // ===== symmetricDiff() =====

    /**
     * Verify symmetric difference contains elements appearing in exactly one set.
     * Assignment 5 equivalent: 3 point(s).
     */
    @Test
    public void testSymmetricDiffPartiallyOverlappingSets() {
        IntegerSet a = set(-3, 0, 8, 15);
        IntegerSet b = set(-3, 8, 11, 22);
        assertResult(a.symmetricDiff(b), a, b, 0, 11, 15, 22);
        assertResult(b.symmetricDiff(a), b, a, 0, 11, 15, 22);
        assertContents(a, -3, 0, 8, 15);
        assertContents(b, -3, 8, 11, 22);
    }

    /**
     * Verify symmetric difference of identical sets is empty.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testSymmetricDiffIdenticalSets() {
        IntegerSet a = set(1, 2);
        IntegerSet b = set(2, 1);
        assertResult(a.symmetricDiff(b), a, b);
        assertResult(a.symmetricDiff(a), a, a);
        assertContents(a, 1, 2);
        assertContents(b, 1, 2);
    }

    /**
     * Verify behavior with an empty set and with disjoint sets.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testSymmetricDiffEmptyAndDisjointSets() {
        IntegerSet a = set(1);
        IntegerSet b = set(2, 3);
        IntegerSet empty = new IntegerSet();
        assertResult(empty.symmetricDiff(b), empty, b, 2, 3);
        assertResult(b.symmetricDiff(empty), b, empty, 2, 3);
        assertResult(a.symmetricDiff(b), a, b, 1, 2, 3);
        assertResult(empty.symmetricDiff(empty), empty, empty);
        assertContents(a, 1);
        assertContents(b, 2, 3);
        assertContents(empty);
    }

    /**
     * Modify the result and verify both input sets remain unchanged.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testSymmetricDiffIndependentResult() {
        IntegerSet a = set(1, 2);
        IntegerSet b = set(2, 3);
        IntegerSet result = a.symmetricDiff(b);
        assertResult(result, a, b, 1, 3);
        assertContents(a, 1, 2);
        assertContents(b, 2, 3);

        result.remove(1);
        result.add(99);
        assertContents(result, 3, 99);
        assertContents(a, 1, 2);
        assertContents(b, 2, 3);

        a.clear();
        b.add(77);
        assertContents(result, 3, 99);
        assertContents(a);
        assertContents(b, 2, 3, 77);
    }

    // ===== isEmpty() =====

    /**
     * Verify a newly constructed set is empty.
     * Assignment 5 equivalent: 1 point(s).
     */
    @Test
    public void testIsEmptyNewSet() {
        IntegerSet s = new IntegerSet();
        assertTrue(s.isEmpty());
    }

    /**
     * Verify a set with an element is not empty.
     * Assignment 5 equivalent: 1 point(s).
     */
    @Test
    public void testIsEmptyPopulatedSet() {
        IntegerSet s = set(0);
        assertFalse(s.isEmpty());
    }

    /**
     * Clear a populated set and verify it is empty.
     * Assignment 5 equivalent: 1 point(s).
     */
    @Test
    public void testIsEmptyAfterClear() {
        IntegerSet s = set(-1, 0, 1);
        assertFalse(s.isEmpty());
        s.clear();
        assertTrue(s.isEmpty());
        assertEquals(0, s.length());
    }

    // ===== toString() =====

    /**
     * Verify elements appear in ascending numeric order regardless of insertion order.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testToStringAscendingOrder() {
        IntegerSet s = set(8, -3, 15, 0, -20, 2);
        assertEquals("[-20, -3, 0, 2, 8, 15]", s.toString());
        assertContents(s, -20, -3, 0, 2, 8, 15);
    }

    /**
     * Verify an empty set formats as [].
     * Assignment 5 equivalent: 1 point(s).
     */
    @Test
    public void testToStringEmptyFormatting() {
        IntegerSet s = new IntegerSet();
        assertEquals("[]", s.toString());
    }

    /**
     * Verify comma-space formatting, brackets, and duplicate suppression.
     * Assignment 5 equivalent: 2 point(s).
     */
    @Test
    public void testToStringExactSpacingAndDuplicates() {
        IntegerSet s = set(2, 1, 2, 3);
        assertEquals("[1, 2, 3]", s.toString());
        assertEquals("[-7]", set(-7, -7).toString());
    }


    /** Creates a fresh test fixture using only the public interface. */
    private static IntegerSet set(int... values) {
        IntegerSet result = new IntegerSet();
        for (int value : values) {
            result.add(value);
        }
        return result;
    }

    /** Checks exact membership and size without relying on equals or toString. */
    private static void assertContents(IntegerSet actual, int... expected) {
        assertEquals(expected.length, actual.length(), "Number of distinct elements");
        for (int value : expected) {
            assertTrue(actual.contains(value), "Missing expected element: " + value);
        }
    }

    /** Checks that an operation returns the expected set in a new object. */
    private static void assertResult(IntegerSet result, IntegerSet a,
            IntegerSet b, int... expected) {
        assertNotSame(a, result, "Result must be independent of the receiver");
        assertNotSame(b, result, "Result must be independent of the argument");
        assertContents(result, expected);
    }
}
