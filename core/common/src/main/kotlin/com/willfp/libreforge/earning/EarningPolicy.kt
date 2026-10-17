package com.willfp.libreforge.earning

import org.quartz.CronExpression
import java.time.ZoneId
import java.util.Date
import java.util.TimeZone

internal enum class TimerStart {
    FIRST_EARNING,
    LIMIT_REACHED
}

internal data class EarningPolicy(
    val limit: Double,
    val duration: Long = 0,
    val timerStart: TimerStart = TimerStart.FIRST_EARNING,
    val cron: String? = null,
    val timezone: String = ZoneId.systemDefault().id
) {
    val kind: String = if (cron == null) "interval" else "cron"
    val signature: String = if (cron == null) "$duration:$timerStart" else "$cron@$timezone"

    private val schedule = cron?.let {
        CronExpression(it).apply {
            timeZone = TimeZone.getTimeZone(ZoneId.of(timezone))
        }
    }

    init {
        require(limit.isFinite() && limit >= 0) { "limit must be a finite non-negative number" }
        require(cron != null || duration > 0) { "duration must be positive" }

        if (cron == null) {
            Math.addExact(System.currentTimeMillis(), duration)
        }

        require(schedule == null || nextAfter(System.currentTimeMillis()) != null) {
            "cron must have a future reset time"
        }
    }

    fun nextAfter(time: Long): Long? {
        return schedule?.getNextValidTimeAfter(Date(time))?.time
    }
}

internal fun parseEarningDuration(input: String): Long {
    require(Regex("(?:[0-9]+[smhdw])+").matches(input)) {
        "duration must use units s, m, h, d or w, e.g. 30s or 1h30m"
    }

    val total = Regex("([0-9]+)([smhdw])").findAll(input).fold(0L) { sum, match ->
        val unit = when (match.groupValues[2]) {
            "s" -> 1000L
            "m" -> 60_000L
            "h" -> 3_600_000L
            "d" -> 86_400_000L
            else -> 604_800_000L
        }

        Math.addExact(sum, Math.multiplyExact(match.groupValues[1].toLong(), unit))
    }

    require(total > 0) { "duration must be positive" }

    return total
}
