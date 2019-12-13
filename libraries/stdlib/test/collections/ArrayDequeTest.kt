/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package collections

import test.collections.behaviors.iteratorBehavior
import test.collections.compare
import kotlin.test.*

class ArrayDequeTest {
    @Test
    fun arrayDequeInit() {
        listOf(ArrayDeque<Int>(), ArrayDeque(10)).forEach { deque ->
            assertTrue(deque.isEmpty())
            assertNull(deque.firstOrNull())
            assertNull(deque.lastOrNull())
            assertNull(deque.removeFirstOrNull())
            assertNull(deque.removeLastOrNull())
        }

        ArrayDeque(listOf(0, 1, 2, 3, 4)).let { deque ->
            assertFalse(deque.isEmpty())
            assertEquals(0, deque.firstOrNull())
            assertEquals(4, deque.lastOrNull())
            assertEquals(0, deque.removeFirstOrNull())
            assertEquals(4, deque.removeLastOrNull())
        }
    }

    @Test
    fun size() {
        listOf(ArrayDeque<Int>(), ArrayDeque(10)).forEach { deque ->
            // head == tail
            assertEquals(0, deque.size)
            // head > tail
            deque.addFirst(-1)
            assertEquals(1, deque.size)
            // buffer expansion, head < tail
            deque.addAll(listOf(1, 2, 3, 4, 5, 6, 7))
            assertEquals(8, deque.size)
        }

        ArrayDeque(listOf(0, 1, 2, 3, 4)).let { deque ->
            // head < tail
            assertEquals(5, deque.size)
            // head > tail
            deque.addFirst(-1)
            assertEquals(6, deque.size)
            // buffer expansion, head < tail
            deque.addAll(listOf(5, 6, 7))
            assertEquals(9, deque.size)
        }
    }

    @Test
    fun contains() {
        val deque = ArrayDeque(listOf(0, 1, 2, 3, 4))
        // head < tail
        assertTrue(deque.contains(0))
        assertTrue(deque.contains(1))
        assertTrue(deque.contains(2))
        assertTrue(deque.contains(3))
        assertTrue(deque.contains(4))
        assertFalse(deque.contains(-1))
        assertFalse(deque.contains(5))

        // head > tail
        deque.addFirst(-1)
        deque.addLast(5)

        assertTrue(deque.contains(0))
        assertTrue(deque.contains(1))
        assertTrue(deque.contains(2))
        assertTrue(deque.contains(3))
        assertTrue(deque.contains(4))
        assertTrue(deque.contains(-1))
        assertTrue(deque.contains(5))

        // remove, head > tail
        deque.remove(2)

        assertTrue(deque.contains(1))
        assertFalse(deque.contains(2))
        assertTrue(deque.contains(3))

        // remove, head < tail
        deque.remove(-1)

        assertTrue(deque.contains(5))
        assertFalse(deque.contains(-1))
        assertTrue(deque.contains(0))
    }

    @Test
    fun clear() {
        val deque = ArrayDeque<Int>(10)
        assertTrue(deque.isEmpty())
        deque.clear()
        assertTrue(deque.isEmpty())
        deque.addLast(0)
        deque.addLast(1)
        deque.addLast(2)
        deque.clear()
        assertTrue(deque.isEmpty())
        deque.addFirst(-1)
        deque.addLast(0)
        deque.addLast(1)
        deque.clear()
        assertTrue(deque.isEmpty())
    }

    @Test
    fun removeElement() { // unify with removeAt
        val deque = ArrayDeque<Int>()
        deque.addLast(0)
        deque.addLast(1)
        deque.addLast(2)
        deque.addLast(3)
        assertTrue(deque.contains(2))
        deque.remove(2)
        assertFalse(deque.contains(2))

        deque.addFirst(-1)
        assertTrue(deque.containsAll(listOf(-1, 0, 1, 3)))
        deque.remove(-1)
        assertTrue(deque.containsAll(listOf(0, 1, 3)))
        deque.remove(0)
        assertTrue(deque.containsAll(listOf(1, 3)))
    }

    @Test
    fun iterator() {
        val deque = ArrayDeque<Int>()

        deque.addLast(0)
        deque.addLast(1)
        deque.addLast(2)
        deque.addLast(3)
        compare(deque.iterator(), listOf(0, 1, 2, 3).iterator()) { iteratorBehavior() }

        deque.addFirst(-1)
        compare(deque.iterator(), listOf(-1, 0, 1, 2, 3).iterator()) { iteratorBehavior() }
    }

//    @Test
//    fun descendingIterator() {
//        val deque = ArrayDeque<Int>()
//
//        deque.addLast(0)
//        deque.addLast(1)
//        deque.addLast(2)
//        deque.addLast(3)
//        compare(deque.descendingIterator(), listOf(3, 2, 1, 0).iterator()) { iteratorBehavior() }
//
//        deque.addFirst(-1)
//        compare(deque.descendingIterator(), listOf(3, 2, 1, 0, -1).iterator()) { iteratorBehavior() }
//    }

    @Test
    fun first() {
        val deque = ArrayDeque<Int>()
        assertFailsWith<NoSuchElementException> { deque.first() }

        deque.addLast(0)
        deque.addLast(1)
        assertEquals(0, deque.first())

        deque.addFirst(-1)
        assertEquals(-1, deque.first())

        deque.removeFirst()
        assertEquals(0, deque.first())

        deque.clear()
        assertFailsWith<NoSuchElementException> { deque.first() }
    }

    @Test
    fun firstOrNull() {
        val deque = ArrayDeque<Int>()
        assertNull(deque.firstOrNull())

        deque.addLast(0)
        deque.addLast(1)
        assertEquals(0, deque.firstOrNull())

        deque.addFirst(-1)
        assertEquals(-1, deque.firstOrNull())

        deque.removeFirst()
        assertEquals(0, deque.firstOrNull())

        deque.clear()
        assertNull(deque.firstOrNull())
    }

    @Test
    fun last() {
        val deque = ArrayDeque<Int>()
        assertFailsWith<NoSuchElementException> { deque.last() }

        deque.addLast(0)
        deque.addLast(1)
        assertEquals(1, deque.last())

        deque.addFirst(-1)
        assertEquals(1, deque.last())

        deque.removeLast()
        assertEquals(0, deque.last())

        deque.clear()
        assertFailsWith<NoSuchElementException> { deque.last() }
    }

    @Test
    fun lastOrNull() {
        val deque = ArrayDeque<Int>()
        assertNull(deque.lastOrNull())

        deque.addLast(0)
        deque.addLast(1)
        assertEquals(1, deque.lastOrNull())

        deque.addFirst(-1)
        assertEquals(1, deque.lastOrNull())

        deque.removeLast()
        assertEquals(0, deque.lastOrNull())

        deque.clear()
        assertNull(deque.lastOrNull())
    }

    @Test
    fun addFirst() {
        val deque = ArrayDeque<Int>()

        // head > tail
        listOf(-1, -2, -3).forEach {
            deque.addFirst(it)
            assertEquals(it, deque.first())
        }

        // head < tail
        listOf(-1, -2).forEach {
            assertEquals(it, deque.removeLast())
        }

        listOf(-4, -5, -6, -7, -8, -9).forEach {
            deque.addFirst(it)
            assertEquals(it, deque.first())
        }

        // buffer expansion, head < tail
        deque.addFirst(-10)
        assertEquals(-10, deque.first())
    }

    @Test
    fun addLast() {
        val deque = ArrayDeque<Int>()

        // head < tail
        listOf(0, 1, 2).forEach {
            deque.addLast(it)
            assertEquals(it, deque.last())
        }

        // head > tail
        listOf(0, 1).forEach {
            assertEquals(it, deque.removeFirst())
        }
        listOf(3, 4, 5, 6, 7, 8).forEach {
            deque.addLast(it)
            assertEquals(it, deque.last())
        }

        // buffer expansion, head < tail
        deque.addLast(9)
        assertEquals(9, deque.last())
    }

    @Test
    fun removeFirst() {
        val deque = ArrayDeque<Int>()
        assertFailsWith<NoSuchElementException> { deque.removeFirst() }

        deque.addLast(0)
        deque.addFirst(-1)
        deque.addFirst(-2)
        deque.addLast(1)

        assertEquals(-2, deque.removeFirst())
        assertEquals(-1, deque.removeFirst())
        assertEquals(0, deque.removeFirst())
        assertEquals(1, deque.removeFirst())

        assertFailsWith<NoSuchElementException> { deque.removeFirst() }
    }

    @Test
    fun removeFirstOrNull() {
        val deque = ArrayDeque<Int>()
        assertNull(deque.removeFirstOrNull())

        deque.addLast(0)
        deque.addFirst(-1)
        deque.addFirst(-2)
        deque.addLast(1)

        assertEquals(-2, deque.removeFirstOrNull())
        assertEquals(-1, deque.removeFirstOrNull())
        assertEquals(0, deque.removeFirstOrNull())
        assertEquals(1, deque.removeFirstOrNull())

        assertNull(deque.removeFirstOrNull())
    }

    @Test
    fun removeLast() {
        val deque = ArrayDeque<Int>()
        assertFailsWith<NoSuchElementException> { deque.removeLast() }

        deque.addLast(0)
        deque.addFirst(-1)
        deque.addFirst(-2)
        deque.addLast(1)

        assertEquals(1, deque.removeLast())
        assertEquals(0, deque.removeLast())
        assertEquals(-1, deque.removeLast())
        assertEquals(-2, deque.removeLast())
    }

    @Test
    fun removeLastOrNull() {
        val deque = ArrayDeque<Int>()
        assertNull(deque.removeLastOrNull())

        deque.addLast(0)
        deque.addFirst(-1)
        deque.addFirst(-2)
        deque.addLast(1)

        assertEquals(1, deque.removeLastOrNull())
        assertEquals(0, deque.removeLastOrNull())
        assertEquals(-1, deque.removeLastOrNull())
        assertEquals(-2, deque.removeLastOrNull())

        assertNull(deque.removeLastOrNull())
    }

    @Test
    fun bufferExpansion() {
        val deque = ArrayDeque<Int>()

        deque.addAll(listOf(0, 1, 2, 3, 4, 5, 6, 7))
        assertEquals(listOf(0, 1, 2, 3, 4, 5, 6, 7), deque.toList())

        listOf(-1, -2, -3, -4, -5, -6, -7, -8).forEach { deque.addFirst(it) }
        assertEquals(listOf(-8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7), deque.toList())
    }

    // MutableList operations
    @Test
    fun insert() {
        val deque = ArrayDeque(listOf(0, 1, 2, 3, 4, 5)).apply { removeFirst() }
        // Move first elements
        // head < tail
        deque.add(1, -1)
        assertEquals(listOf(/**/1, -1, 2, 3, 4, 5), deque.toList())
        deque.add(2, -2)
        assertEquals(listOf(1, /**/-1, -2, 2, 3, 4, 5), deque.toList())

        // head > tail
        // internalIndex < tail
        deque.removeLast()
        deque.add(2, -3)
        assertEquals(listOf(1, -1, /**/-3, -2, 2, 3, 4), deque.toList())

        deque.removeLast()
        deque.add(2, -4)
        assertEquals(listOf(1, -1, -4, /**/-3, -2, 2, 3), deque.toList())

        // internalIndex > tail
        deque.removeLast()
        deque.add(1, -5)
        assertEquals(listOf(1, -5, -1, -4, /**/-3, -2, 2), deque.toList())

        // buffer expansion
        deque.add(2, -6)
        assertEquals(listOf(/**/1, -5, -6, -1, -4, -3, -2, 2), deque.toList())

        // Move last elements
        // head < tail
        deque.add(5, -7)
        assertEquals(listOf(/**/1, -5, -6, -1, -4, -7, -3, -2, 2), deque.toList())

        // head > tail
        // internalIndex < tail
        deque.add(0, -8)
        assertEquals(listOf(-8, /**/1, -5, -6, -1, -4, -7, -3, -2, 2), deque.toList())
        deque.add(9, -9)
        assertEquals(listOf(-8, /**/1, -5, -6, -1, -4, -7, -3, -2, -9, 2), deque.toList())

        // internalIndex > tail
        listOf(-6, -1, -4, -7, -3, -2, -9, 2).asReversed().forEach { deque.remove(it) }
        assertEquals(listOf(-8, /**/1, -5), deque.toList())
        listOf(-10, -11, -12, -13, -14, -15, -16).asReversed().forEach { deque.addFirst(it) }
        assertEquals(listOf(-10, -11, -12, -13, -14, -15, -16, -8, /**/1, -5), deque.toList())

        deque.add(8, -17)
        assertEquals(listOf(-10, -11, -12, -13, -14, -15, -16, -8, /**/-17, 1, -5), deque.toList())

        deque.add(6, -18)
        assertEquals(listOf(-10, -11, -12, -13, -14, -15, -18, -16, /**/-8, -17, 1, -5), deque.toList())
    }

    @Test
    fun removeAt() {
        // Move first elements
        // head < tail

        // head > tail
        // internalIndex < tail

        // internalIndex > tail


        // Move last elements
        // head < tail

        // head > tail
        // internalIndex < tail

        // internalIndex > tail
    }

    @Test
    fun indexOf() {
        // head < tail

        // head > tail
        // internalIndex < tail

        // internalIndex > tail
    }

    @Test
    fun addAll() {
        // head < tail

        // head > tail
    }

    @Test
    fun insertAll() {
        // Move first elements
        // head < tail

        // head > tail
        // internalIndex < tail

        // internalIndex > tail


        // Move last elements
        // head < tail

        // head > tail
        // internalIndex < tail

        // internalIndex > tail
    }

    @Test
    fun listIterator() {
        // head < tail

        // head > tail

        // add -> double capacity

        // remove
    }

    @Test
    fun removeAll() {

    }

    @Test
    fun retainAll() {

    }

    @Test
    fun set() {
        // head < tail

        // head > tail
        // internalIndex < tail

        // internalIndex > tail
    }

    @Test
    fun get() {
        // head < tail

        // head > tail
        // internalIndex < tail

        // internalIndex > tail
    }

    @Test
    fun subList() {
        // head < tail

        // head > tail
        // internalIndex < tail

        // internalIndex > tail
    }
}