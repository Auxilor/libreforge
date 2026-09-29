package com.willfp.libreforge.filters.impl

import com.willfp.libreforge.dates.DateTimeMatcher
import com.willfp.libreforge.dates.DateTimeMatchers
import com.willfp.libreforge.filters.DateTimeFilter

object FilterIsDayOfWeek : DateTimeFilter.Values("is_day_of_week") {
    override val description = "Matches when it is one of the given days of the week."

    override val additionalInfo = listOf(
        "Day names (friday) or numbers from 1 (Monday) to 7 (Sunday). A single value or a list.",
        "The date and time are worked out in the timezone set by dates.timezone in config.yml."
    )

    override fun compile(value: List<String>): DateTimeMatcher = DateTimeMatchers.daysOfWeek(value)
}
