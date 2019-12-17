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

    private fun generateArrayDeque(head: Int, tail: Int): ArrayDeque<Int> {
        check(tail > 0)

        val deque = ArrayDeque<Int>()

        repeat(tail) {
            deque.addLast(it)
            if (it < head) deque.removeFirst()
        }
        repeat(-head) { deque.addFirst(-(it + 1)) }

        assertEquals(tail - head, deque.size)

        return deque
    }

    // MutableList operations
    @Test
    fun insert() {
        // Move first elements

        // head < tail, internalIndex > head
        generateArrayDeque(1, 6).let { deque ->
            deque.add(1, 100)
            assertEquals(listOf(/**/1, 100, 2, 3, 4, 5), deque.toList())
        }
        generateArrayDeque(1, 7).let { deque ->
            deque.add(2, 100)
            assertEquals(listOf(/**/1, 2, 100, 3, 4, 5, 6), deque.toList())
        }
        generateArrayDeque(0, 5).let { deque ->
            deque.add(1, 100)
            assertEquals(listOf(0, /**/100, 1, 2, 3, 4), deque.toList())
        }
        generateArrayDeque(0, 6).let { deque ->
            deque.add(2, 100)
            assertEquals(listOf(0, /**/1, 100, 2, 3, 4, 5), deque.toList())
        }
        // buffer expansion
        generateArrayDeque(1, 8).let { deque ->
            deque.add(1, 100)
            assertEquals(listOf(/**/1, 100, 2, 3, 4, 5, 6, 7), deque.toList())
        }
        generateArrayDeque(0, 7).let { deque ->
            deque.add(2, 100)
            assertEquals(listOf(/**/0, 1, 100, 2, 3, 4, 5, 6), deque.toList())
        }

        // head > tail, internalIndex < tail
        generateArrayDeque(-1, 4).let { deque ->
            deque.add(1, 100)
            assertEquals(listOf(-1, 100, /**/0, 1, 2, 3), deque.toList())
        }
        generateArrayDeque(-1, 5).let { deque ->
            deque.add(2, 100)
            assertEquals(listOf(-1, 0, /**/100, 1, 2, 3, 4), deque.toList())
        }
        generateArrayDeque(-1, 5).let { deque ->
            deque.add(1, 100)
            assertEquals(listOf(-1, 100, /**/0, 1, 2, 3, 4), deque.toList())
        }
        generateArrayDeque(-2, 4).let { deque ->
            deque.add(2, 100)
            assertEquals(listOf(-2, -1, 100, /**/0, 1, 2, 3), deque.toList())
        }
        // buffer expansion
        generateArrayDeque(-1, 6).let { deque ->
            deque.add(2, 100)
            assertEquals(listOf(/**/-1, 0, 100, 1, 2, 3, 4, 5), deque.toList())
        }
        generateArrayDeque(-2, 5).let { deque ->
            deque.add(2, 100)
            assertEquals(listOf(/**/-2, -1, 100, 0, 1, 2, 3, 4), deque.toList())
        }

        // head > tail, internalIndex > head
        generateArrayDeque(-2, 3).let { deque ->
            deque.add(1, 100)
            assertEquals(listOf(-2, 100, -1, /**/0, 1, 2), deque.toList())
        }
        generateArrayDeque(-2, 4).let { deque ->
            deque.add(2, 100)
            assertEquals(listOf(-2, -1, 100, /**/0, 1, 2, 3), deque.toList())
        }
        // buffer expansion
        generateArrayDeque(-2, 5).let { deque ->
            deque.add(1, 100)
            assertEquals(listOf(/**/-2, 100, -1, 0, 1, 2, 3, 4), deque.toList())
        }
        generateArrayDeque(-3, 4).let { deque ->
            deque.add(2, 100)
            assertEquals(listOf(/**/-3, -2, 100, -1, 0, 1, 2, 3), deque.toList())
        }

        // Move last elements

        // head < tail, internalIndex > head
        generateArrayDeque(0, 5).let { deque ->
            deque.add(4, 100)
            assertEquals(listOf(/**/0, 1, 2, 3, 100, 4), deque.toList())
        }
        generateArrayDeque(0, 6).let { deque ->
            deque.add(4, 100)
            assertEquals(listOf(/**/0, 1, 2, 3, 100, 4, 5), deque.toList())
        }
        generateArrayDeque(3, 8).let { deque ->
            deque.add(4, 100)
            assertEquals(listOf(3, 4, 5, 6, 100, /**/7), deque.toList())
        }
        generateArrayDeque(2, 8).let { deque ->
            deque.add(4, 100)
            assertEquals(listOf(2, 3, 4, 5, 100, /**/6, 7), deque.toList())
        }
        // buffer expansion
        generateArrayDeque(1, 8).let { deque ->
            deque.add(6, 100)
            assertEquals(listOf(/**/1, 2, 3, 4, 5, 6, 100, 7), deque.toList())
        }
        generateArrayDeque(0, 7).let { deque ->
            deque.add(5, 100)
            assertEquals(listOf(/**/0, 1, 2, 3, 4, 100, 5, 6), deque.toList())
        }

        // head > tail, internalIndex < tail
        generateArrayDeque(-1, 4).let { deque ->
            deque.add(4, 100)
            assertEquals(listOf(-1, /**/0, 1, 2, 100, 3), deque.toList())
        }
        generateArrayDeque(-2, 4).let { deque ->
            deque.add(4, 100)
            assertEquals(listOf(-2, -1, /**/0, 1, 100, 2, 3), deque.toList())
        }
        generateArrayDeque(-4, 1).let { deque ->
            deque.add(4, 100)
            assertEquals(listOf(-4, -3, -2, -1, /**/100, 0), deque.toList())
        }
        generateArrayDeque(-4, 2).let { deque ->
            deque.add(4, 100)
            assertEquals(listOf(-4, -3, -2, -1, /**/100, 0, 1), deque.toList())
        }
        // buffer expansion
        generateArrayDeque(-1, 6).let { deque ->
            deque.add(6, 100)
            assertEquals(listOf(/**/-1, 0, 1, 2, 3, 4, 100, 5), deque.toList())
        }
        generateArrayDeque(-2, 5).let { deque ->
            deque.add(5, 100)
            assertEquals(listOf(/**/-2, -1, 0, 1, 2, 100, 3, 4), deque.toList())
        }

        // head > tail, internalIndex > head
        generateArrayDeque(-5, 1).let { deque ->
            deque.add(4, 100)
            assertEquals(listOf(-5, -4, -3, -2, 100, /**/-1, 0), deque.toList())
        }
        generateArrayDeque(-5, 1).let { deque ->
            deque.add(3, 100)
            assertEquals(listOf(-5, -4, -3, 100, -2, /**/-1, 0), deque.toList())
        }
        generateArrayDeque(-4, 2).let { deque ->
            deque.add(3, 100)
            assertEquals(listOf(-4, -3, -2, 100, /**/-1, 0, 1), deque.toList())
        }
        // buffer expansion
        generateArrayDeque(-6, 1).let { deque ->
            deque.add(5, 100)
            assertEquals(listOf(/**/-6, -5, -4, -3, -2, 100, -1, 0), deque.toList())
        }
        generateArrayDeque(-5, 2).let { deque ->
            deque.add(4, 100)
            assertEquals(listOf(/**/-5, -4, -3, -2, 100, -1, 0, 1), deque.toList())
        }
    }

    @Test
    fun removeAt() {
        // Move first elements

        // head < tail, internalIndex > head
        generateArrayDeque(1, 6).let { deque ->
            deque.removeAt(1)
            assertEquals(listOf(/**/1, 3, 4, 5), deque.toList())
        }
        generateArrayDeque(1, 7).let { deque ->
            deque.removeAt(2)
            assertEquals(listOf(/**/1, 2, 4, 5, 6), deque.toList())
        }

        // head > tail, internalIndex < tail
        generateArrayDeque(-1, 4).let { deque ->
            deque.removeAt(1)
            assertEquals(listOf(/**/-1, 1, 2, 3), deque.toList())
        }
        generateArrayDeque(-1, 5).let { deque ->
            deque.removeAt(2)
            assertEquals(listOf(/**/-1, 0, 2, 3, 4), deque.toList())
        }
        generateArrayDeque(-2, 5).let { deque ->
            deque.removeAt(2)
            assertEquals(listOf(-2, /**/-1, 1, 2, 3, 4), deque.toList())
        }
        generateArrayDeque(-3, 10).let { deque ->
            deque.removeAt(5)
            assertEquals(listOf(-3, -2, /**/-1, 0, 1, 3, 4, 5, 6, 7, 8, 9), deque.toList())
        }

        // head > tail, internalIndex > head
        generateArrayDeque(-2, 3).let { deque ->
            deque.removeAt(1)
            assertEquals(listOf(-2, /**/0, 1, 2), deque.toList())
        }
        generateArrayDeque(-3, 4).let { deque ->
            deque.removeAt(2)
            assertEquals(listOf(-3, -2, /**/0, 1, 2, 3), deque.toList())
        }
        generateArrayDeque(-3, 4).let { deque ->
            deque.removeAt(1)
            assertEquals(listOf(-3, -1, /**/0, 1, 2, 3), deque.toList())
        }

        // Move last elements

        // head < tail, internalIndex > head
        generateArrayDeque(0, 5).let { deque ->
            deque.removeAt(3)
            assertEquals(listOf(/**/0, 1, 2, 4), deque.toList())
        }
        generateArrayDeque(0, 6).let { deque ->
            deque.removeAt(3)
            assertEquals(listOf(/**/0, 1, 2, 4, 5), deque.toList())
        }

        // head > tail, internalIndex < tail
        generateArrayDeque(-1, 4).let { deque ->
            deque.removeAt(3)
            assertEquals(listOf(-1, /**/0, 1, 3), deque.toList())
        }
        generateArrayDeque(-4, 3).let { deque ->
            deque.removeAt(4)
            assertEquals(listOf(-4, -3, -2, -1, /**/1, 2), deque.toList())
        }
        generateArrayDeque(-4, 2).let { deque ->
            deque.removeAt(4)
            assertEquals(listOf(-4, -3, -2, -1, /**/1), deque.toList())
        }

        // head > tail, internalIndex > head
        generateArrayDeque(-5, 1).let { deque ->
            deque.removeAt(4)
            assertEquals(listOf(-5, -4, -3, -2, 0/**/), deque.toList())
        }
        generateArrayDeque(-5, 1).let { deque ->
            deque.removeAt(3)
            assertEquals(listOf(-5, -4, -3, -1, 0/**/), deque.toList())
        }
        generateArrayDeque(-4, 2).let { deque ->
            deque.removeAt(3)
            assertEquals(listOf(-4, -3, -2, 0, /**/1), deque.toList())
        }
        generateArrayDeque(-6, 1).let { deque ->
            deque.removeAt(5)
            assertEquals(listOf(-6, -5, -4, -3, -2, 0/**/), deque.toList())
        }
        generateArrayDeque(-4, 3).let { deque ->
            deque.removeAt(3)
            assertEquals(listOf(-4, -3, -2, 0, /**/1, 2), deque.toList())
        }
    }

    @Test
    fun indexOf() {
        // head < tail
        generateArrayDeque(0, 7).let { deque ->
            (0..6).forEach { assertEquals(it, deque.indexOf(it)) }
            assertEquals(-1, deque.indexOf(100))
        }

        // head > tail
        generateArrayDeque(-4, 3).let { deque ->
            (0..6).forEach { assertEquals(it, deque.indexOf(it - 4)) }
            assertEquals(-1, deque.indexOf(100))
        }
    }

    @Test
    fun addAll() {
        // head < tail
        generateArrayDeque(0, 3).let { deque ->
            deque.addAll(listOf(3, 4, 5))
            assertEquals(listOf(0, 1, 2, 3, 4, 5), deque.toList())

            deque.addAll(6..100)
            assertEquals((0..100).toList(), deque.toList())
        }

        generateArrayDeque(4, 6).let { deque ->
            deque.addAll(listOf(6, 7, 8))
            assertEquals(listOf(4, 5, 6, 7, 8), deque.toList())

            deque.addAll(9..100)
            assertEquals((4..100).toList(), deque.toList())
        }

        // head > tail
        generateArrayDeque(-3, 2).let { deque ->
            deque.addAll(listOf(2, 3))
            assertEquals(listOf(-3, -2, -1, 0, 1, 2, 3), deque.toList())

            deque.addAll(4..100)
            assertEquals((-3..100).toList(), deque.toList())
        }
    }

    @Test
    fun insertAll() {
        // Move first elements
        // head < tail
        generateArrayDeque(0, 4).let { deque ->
            deque.addAll(0, listOf(4, 5))
            assertEquals(listOf(4, 5, /**/0, 1, 2, 3), deque.toList())
        }
        generateArrayDeque(2, 7).let { deque ->
            deque.addAll(2, listOf(100, 101))
            assertEquals(listOf(/**/2, 3, 100, 101, 4, 5, 6), deque.toList())
        }
        generateArrayDeque(2, 12).let { deque ->
            deque.addAll(2, listOf(100, 101, 102))
            assertEquals(listOf(2, 3, 100, /**/101, 102, 4, 5, 6, 7, 8, 9, 10, 11), deque.toList())
        }
        // buffer expansion

        // head > tail, internalIndex < tail

        // head > tail, internalIndex >= head
        generateArrayDeque(-2, 4).let { deque ->
            deque.addAll(0, listOf(6, 7, 8, 9))
            assertEquals(listOf(6, 7, 8, 9, -2, -1, /**/0, 1, 2, 3), deque.toList())
        }


        // Move last elements
        // head < tail

        // head > tail, internalIndex < tail

        // head > tail, internalIndex > head
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

        // head > tail, internalIndex < tail

        // head > tail, internalIndex > head
    }

    @Test
    fun get() {
        // head < tail

        // head > tail, internalIndex < tail

        // head > tail, internalIndex > head
    }

    @Test
    fun subList() {
        // head < tail

        // head > tail, internalIndex < tail

        // head > tail, internalIndex > head
    }
}