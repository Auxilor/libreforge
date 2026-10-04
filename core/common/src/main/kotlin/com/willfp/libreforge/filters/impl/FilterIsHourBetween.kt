package com.willfp.libreforge.filters.impl

import com.willfp.libreforge.dates.DateTimeMatcher
import com.willfp.libreforge.dates.DateTimeMatchers
import com.willfp.libreforge.filters.DateTimeFilter

object FilterIsHourBetween : DateTimeFilter.Between("is_hour_between") {
    override val description = "Matches when the hour is between from and to (inclusive)."

    override val additionalInfo = listOf(
        "Set from and to beneath the filter, e.g. is_hour_between: { from: \"22\", to: \"4\" }.",
        "Hours of the day (0-23); to includes the whole of that hour.",
        "Wraps across midnight when to is earlier than from.",
        "The date and time are worked out in the timezone set by dates.timezone in config.yml."
    )

    override fun compile(from: String, to: String): DateTimeMatcher = DateTimeMatchers.hourBetween(from, to)
}
