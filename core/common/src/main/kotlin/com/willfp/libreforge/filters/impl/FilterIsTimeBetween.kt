package com.willfp.libreforge.filters.impl

import com.willfp.libreforge.dates.DateTimeMatcher
import com.willfp.libreforge.dates.DateTimeMatchers
import com.willfp.libreforge.filters.DateTimeFilter

object FilterIsTimeBetween : DateTimeFilter.Between("is_time_between") {
    override val description = "Matches when the time is between from and to (inclusive)."

    override val additionalInfo = listOf(
        "Set from and to beneath the filter, e.g. is_time_between: { from: \"21:30\", to: \"06:00\" }.",
        "Times as HH:MM (24 hour); to includes the whole of that minute.",
        "Wraps across midnight when to is earlier than from.",
        "The date and time are worked out in the timezone set by dates.timezone in config.yml."
    )

    override fun compile(from: String, to: String): DateTimeMatcher = DateTimeMatchers.timeBetween(from, to)
}
