/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.collections

/**
 * The minimum capacity that we'll use for a newly created deque.
 * Must be a power of 2.
 */
private const val MIN_INITIAL_CAPACITY = 8

@UseExperimental(ExperimentalStdlibApi::class)
public class ArrayDeque<E>(capacity: Int) : AbstractMutableList<E>() {
    private var head: Int = 0
    private var tail: Int = 0
    private var elements: Array<Any?>

    init {
        var initialCapacity = MIN_INITIAL_CAPACITY
        // Find the best power of two to hold elements.
        // Tests "<=" because arrays aren't kept full.
        if (capacity >= initialCapacity) {
            initialCapacity = capacity.takeHighestOneBit() shl 1

            if (initialCapacity < 0)   // Too many elements, must back off
                initialCapacity = initialCapacity ushr 1// Good luck allocating 2 ^ 30 elements
        }
        elements = arrayOfNulls(initialCapacity)
    }

    constructor() : this(0)

    constructor(elements: Collection<E>) : this(elements.size) {
        this.addAll(elements)
    }

    /**
     * Doubles the capacity of this deque.  Call only when full, i.e.,
     * when head and tail have wrapped around to become equal.
     */
    private fun doubleCapacity() {
        check(head == tail)

        val newCapacity = elements.size shl 1
        if (newCapacity < 0)
            throw IllegalStateException("Sorry, deque too big")

        val a = arrayOfNulls<Any?>(newCapacity)
        elements.copyInto(a, 0, head, elements.size)
        elements.copyInto(a, elements.size - head, 0, head)
        head = 0
        tail = elements.size
        elements = a
    }

    private inline val mask: Int
        get() = elements.size - 1

    @kotlin.internal.InlineOnly
    private inline fun internalGet(internalIndex: Int): E {
        @Suppress("UNCHECKED_CAST")
        return elements[internalIndex] as E
    }

    @kotlin.internal.InlineOnly
    private inline fun internalIndex(index: Int): Int = (head + index) and mask

    @kotlin.internal.InlineOnly
    private inline fun incremented(index: Int): Int = (index + 1) and mask

    @kotlin.internal.InlineOnly
    private inline fun decremented(index: Int): Int = (index - 1) and mask

    override val size: Int
        get() = (tail - head) and mask

    override fun isEmpty(): Boolean = head == tail

    fun first(): E = if (isEmpty()) throw NoSuchElementException() else internalGet(head)

    fun firstOrNull(): E? = if (isEmpty()) null else internalGet(head)

    fun last(): E = if (isEmpty()) throw NoSuchElementException() else internalGet(decremented(tail))

    fun lastOrNull(): E? = if (isEmpty()) null else internalGet(decremented(tail))

    fun addFirst(element: E): Boolean {
        head = decremented(head)
        elements[head] = element

        if (head == tail) {
            doubleCapacity()
        }
        return true
    }

    fun addLast(element: E): Boolean {
        elements[tail] = element
        tail = incremented(tail)

        if (head == tail) {
            doubleCapacity()
        }
        return true
    }

    fun removeFirst(): E {
        if (isEmpty())
            throw NoSuchElementException()

        val element = internalGet(head)
        elements[head] = null
        head = incremented(head)
        return element
    }

    fun removeFirstOrNull(): E? = if (isEmpty()) null else removeFirst()

    fun removeLast(): E {
        if (isEmpty())
            throw NoSuchElementException()

        tail = decremented(tail)
        val element = internalGet(tail)
        elements[tail] = null
        return element
    }

    fun removeLastOrNull(): E? = if (isEmpty()) null else removeLast()

    // MutableList, MutableCollection
    override fun add(element: E): Boolean = addLast(element)

    override fun add(index: Int, element: E) {
        AbstractList.checkPositionIndex(index, size)

        if (index == 0) {
            addFirst(element)
            return
        } else if (index == size) {
            addLast(element)
            return
        }

        if (index < size shr 1) {
            // closer to the first element -> move first elements
            val internalIndex = internalIndex(index)

            if (internalIndex < head) { // head can't be zero
                elements.copyInto(elements, head - 1, head, elements.size)

                if (internalIndex != 0) {
                    elements[elements.size - 1] = elements[0]
                    elements.copyInto(elements, 0, 1, internalIndex)
                }
            } else { // internalIndex > head, head may be zero
                elements[decremented(head)] = elements[head]
                elements.copyInto(elements, head, head + 1, internalIndex)
            }

            elements[decremented(internalIndex)] = element
            head = decremented(head)
        } else {
            // closer to the last element -> move last elements
            val internalIndex = internalIndex(index)

            if (internalIndex > tail) { // internalIndex may be `elements.size - 1`
                elements.copyInto(elements, 1, 0, tail)
                elements[0] = elements[elements.size - 1]
                elements.copyInto(elements, internalIndex + 1, internalIndex, elements.size - 1)
            } else { // internalIndex < tail
                elements.copyInto(elements, internalIndex + 1, internalIndex, tail)
            }

            elements[internalIndex] = element
            tail = incremented(tail)
        }

        if (head == tail) {
            doubleCapacity()
        }
    }

//    override fun addAll(index: Int, elements: Collection<E>): Boolean {
//        TODO()
//    }

    override fun get(index: Int): E {
        AbstractList.checkElementIndex(index, size)

        return internalGet(internalIndex(index))
    }

    override fun set(index: Int, element: E): E {
        AbstractList.checkElementIndex(index, size)

        val internalIndex = internalIndex(index)
        val oldElement = internalGet(internalIndex(index))
        elements[internalIndex] = element

        return oldElement
    }

    override fun contains(element: E): Boolean = indexOf(element) != -1

    override fun indexOf(element: E): Int {
        if (head < tail) {
            for (index in head until tail) {
                if (element == elements[index]) return index - head
            }
        } else if (head > tail) {
            for (index in head until elements.size) {
                if (element == elements[index]) return index - head
            }
            for (index in 0 until tail) {
                if (element == elements[index]) return index + elements.size - head
            }
        }

        return -1
    }

    override fun lastIndexOf(element: E): Int {
        if (head < tail) {
            for (index in tail - 1 downTo head) {
                if (element == elements[index]) return index - head
            }
        } else if (head > tail) {
            for (index in tail - 1 downTo 0) {
                if (element == elements[index]) return index + elements.size - head
            }
            for (index in elements.size - 1 downTo head) {
                if (element == elements[index]) return index - head
            }
        }

        return -1
    }

    override fun remove(element: E): Boolean {
        val index = indexOf(element)
        if (index == -1) return false
        removeAt(index)
        return true
    }

    override fun removeAt(index: Int): E {
        AbstractList.checkElementIndex(index, size)

        if (index == 0) {
            return removeFirst()
        } else if (index == lastIndex) {
            return removeLast()
        }

        val element: E
        if (index < size shr 1) {
            // closer to the first element -> move first elements
            val internalIndex = internalIndex(index)
            element = internalGet(internalIndex)

            if (internalIndex < head) {
                elements.copyInto(elements, 1, 0, internalIndex)
                elements[0] = elements[elements.size - 1]
                elements.copyInto(elements, head + 1, head, elements.size - 1)
            } else {
                elements.copyInto(elements, head + 1, head, internalIndex)
            }

            elements[head] = null
            head = incremented(head)
        } else {
            // closer to the last element -> move last elements
            val internalIndex = internalIndex(index)
            element = internalGet(internalIndex)

            tail = decremented(tail)

            if (internalIndex > tail) {
                elements.copyInto(elements, internalIndex, internalIndex + 1, elements.size)
                elements[elements.size - 1] = elements[0]
                elements.copyInto(elements, 0, 1, tail + 1)
            } else {
                elements.copyInto(elements, internalIndex, internalIndex + 1, tail + 1)
            }

            elements[tail] = null
        }

        return element
    }

    override fun clear() {
        if (head < tail) {
            elements.fill(null, head, tail)
        } else if (head > tail) {
            elements.fill(null, head, elements.size)
            elements.fill(null, 0, tail)
        }
        head = 0
        tail = 0
    }
}