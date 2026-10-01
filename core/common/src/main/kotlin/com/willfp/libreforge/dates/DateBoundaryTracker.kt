package com.willfp.libreforge.dates

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * A span of calendar time with start and end triggers.
 */
enum class DatePeriod {
    HOUR {
        override fun startOf(dateTime: LocalDateTime): LocalDateTime = dateTime.truncatedTo(ChronoUnit.HOURS)

        override fun isFinalMinute(dateTime: LocalDateTime): Boolean = dateTime.minute == 59
    },

    DAY {
        override fun startOf(dateTime: LocalDateTime): LocalDateTime = dateTime.truncatedTo(ChronoUnit.DAYS)

        override fun isFinalMinute(dateTime: LocalDateTime): Boolean = dateTime.hour == 23 && dateTime.minute == 59
    },

    MONTH {
        override fun startOf(dateTime: LocalDateTime): LocalDateTime =
            dateTime.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1)

        override fun isFinalMinute(dateTime: LocalDateTime): Boolean =
            DAY.isFinalMinute(dateTime) && dateTime.dayOfMonth == dateTime.toLocalDate().lengthOfMonth()
    };

    /**
     * When the period containing [dateTime] began.
     */
    abstract fun startOf(dateTime: LocalDateTime): LocalDateTime

    /**
     * If [dateTime] is in the last minute of its period.
     */
    abstract fun isFinalMinute(dateTime: LocalDateTime): Boolean
}

/**
 * Which side of a [DatePeriod] a boundary is on.
 */
enum class DateEdge {
    START,
    END
}

/**
 * A period starting or ending at [dateTime].
 *
 * [periodStart] identifies the period, e.g. midnight on the 1st for a month.
 */
data class DateBoundary(
    val period: DatePeriod,
    val edge: DateEdge,
    val periodStart: LocalDateTime,
    val dateTime: LocalDateTime
)

/**
 * Turns a series of clock readings into [DateBoundary]s.
 *
 * Starts are reported on the first reading in a new period. Ends are reported on the first
 * reading in a period's final minute, so anything checking the time then still sees the
 * period that's ending; a final minute that's missed entirely (e.g. the server was stalled)
 * is skipped rather than reported late.
 */
class DateBoundaryTracker {
    private var previous: LocalDateTime? = null

    // The start of the latest period each end was reported for, so each end is reported once
    private val lastEnded = mutableMapOf<DatePeriod, LocalDateTime>()

    /**
     * Record the clock reading [now], returning the boundaries crossed since the last one:
     * ends first, then starts, each in [DatePeriod] order.
     *
     * The first reading only sets the baseline, so a restart doesn't re-fire anything.
     */
    fun advance(now: LocalDateTime): List<DateBoundary> {
        val last = previous
        previous = now

        if (last == null) {
            for (period in DatePeriod.entries) {
                if (period.isFinalMinute(now)) {
                    lastEnded[period] = period.startOf(now)
                }
            }

            return emptyList()
        }

        val ends = DatePeriod.entries.mapNotNull { period ->
            val periodStart = period.startOf(now)

            if (!period.isFinalMinute(now) || lastEnded[period] == periodStart) {
                return@mapNotNull null
            }

            lastEnded[period] = periodStart
            DateBoundary(period, DateEdge.END, periodStart, now)
        }

        val starts = DatePeriod.entries.mapNotNull { period ->
            val periodStart = period.startOf(now)

            // Only forwards, so the clock going back (e.g. daylight saving ending) doesn't re-fire
            if (periodStart <= period.startOf(last)) {
                return@mapNotNull null
            }

            DateBoundary(period, DateEdge.START, periodStart, now)
        }

        return ends + starts
    }
}
