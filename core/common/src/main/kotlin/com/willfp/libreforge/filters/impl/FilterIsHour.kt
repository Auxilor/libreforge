package com.willfp.libreforge.filters.impl

import com.willfp.libreforge.dates.DateTimeMatcher
import com.willfp.libreforge.dates.DateTimeMatchers
import com.willfp.libreforge.filters.DateTimeFilter

object FilterIsHour : DateTimeFilter.Values("is_hour") {
    override val description = "Matches when it is one of the given hours."

    override val additionalInfo = listOf(
        "Hours of the day (0-23). A single value or a list.",
        "The date and time are worked out in the timezone set by dates.timezone in config.yml."
    )

    override fun compile(value: List<String>): DateTimeMatcher = DateTimeMatchers.hours(value)
}
