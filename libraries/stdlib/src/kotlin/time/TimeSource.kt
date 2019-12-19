/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.time

/**
 * A source of time for measuring time intervals.
 *
 * The only operation provided by the clock is [markNow]. It returns a [TimeMark], which can be used to query the elapsed time later.
 *
 * @see [measureTime]
 * @see [measureTimedValue]
 */
@SinceKotlin("1.3")
@ExperimentalTime
public interface TimeSource {
    /**
     * Marks a time point on this clock.
     *
     * The returned [TimeMark] instance encapsulates captured time point and allows querying
     * the duration of time interval [elapsed][TimeMark.elapsedNow] from that point.
     */
    public fun markNow(): TimeMark


    object Monotonic : TimeSource by MonotonicTimeSource

    public companion object {

    }
}

/**
 * Represents a time point notched on a particular [TimeSource]. Remains bound to the time source it was taken from
 * and allows querying for the duration of time elapsed from that point (see the function [elapsedNow]).
 */
@SinceKotlin("1.3")
@ExperimentalTime
public abstract class TimeMark {
    /**
     * Returns the amount of time passed from this mark measured with the time source from which this mark was taken.
     *
     * Note that the value returned by this function can change on subsequent invocations.
     */
    public abstract fun elapsedNow(): Duration

    /**
     * Returns a time mark on the same clock that is ahead of this clock mark by the specified [duration].
     *
     * The returned clock mark is more _late_ when the [duration] is positive, and more _early_ when the [duration] is negative.
     */
    public open operator fun plus(duration: Duration): TimeMark = AdjustedTimeMark(this, duration)

    /**
     * Returns a clock mark on the same clock that is behind this clock mark by the specified [duration].
     *
     * The returned clock mark is more _early_ when the [duration] is positive, and more _late_ when the [duration] is negative.
     */
    public open operator fun minus(duration: Duration): TimeMark = plus(-duration)


    /**
     * Returns true if this clock mark has passed according to the clock from which this mark was taken.
     *
     * Note that the value returned by this function can change on subsequent invocations.
     * If the clock is monotonic, it can change only from `false` to `true`, namely, when the clock mark becomes behind the current point of the clock.
     */
    public fun hasPassedNow(): Boolean = !elapsedNow().isNegative()

    /**
     * Returns false if this clock mark has not passed according to the clock from which this mark was taken.
     *
     * Note that the value returned by this function can change on subsequent invocations.
     * If the clock is monotonic, it can change only from `true` to `false`, namely, when the clock mark becomes behind the current point of the clock.
     */
    public fun hasNotPassedNow(): Boolean = elapsedNow().isNegative()
}


@ExperimentalTime
@SinceKotlin("1.3")
@kotlin.internal.InlineOnly
@Deprecated("Subtracting one ClockMark from another is not a well defined operation because these clock marks could have been obtained from the different clocks.", level = DeprecationLevel.ERROR)
public inline operator fun TimeMark.minus(other: TimeMark): Duration = throw Error("Operation is disallowed.")

@ExperimentalTime
@SinceKotlin("1.3")
@kotlin.internal.InlineOnly
@Deprecated("Comparing one ClockMark to another is not a well defined operation because these clock marks could have been obtained from the different clocks.", level = DeprecationLevel.ERROR)
public inline operator fun TimeMark.compareTo(other: TimeMark): Int = throw Error("Operation is disallowed.")


@ExperimentalTime
private class AdjustedTimeMark(val mark: TimeMark, val adjustment: Duration) : TimeMark() {
    override fun elapsedNow(): Duration = mark.elapsedNow() - adjustment

    override fun plus(duration: Duration): TimeMark = AdjustedTimeMark(mark, adjustment + duration)
}