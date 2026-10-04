package com.willfp.libreforge.filters.impl

import com.willfp.libreforge.dates.DateTimeMatcher
import com.willfp.libreforge.dates.DateTimeMatchers
import com.willfp.libreforge.filters.DateTimeFilter

object FilterIsDayOfMonth : DateTimeFilter.Values("is_day_of_month") {
    override val description = "Matches when it is one of the given days of the month."

    override val additionalInfo = listOf(
        "Days of the month (1-31). A single value or a list.",
        "The date and time are worked out in the timezone set by dates.timezone in config.yml."
    )

    override fun compile(value: List<String>): DateTimeMatcher = DateTimeMatchers.daysOfMonth(value)
}
