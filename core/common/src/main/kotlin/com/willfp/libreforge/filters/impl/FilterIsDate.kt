package com.willfp.libreforge.filters.impl

import com.willfp.libreforge.dates.DateTimeMatcher
import com.willfp.libreforge.dates.DateTimeMatchers
import com.willfp.libreforge.filters.DateTimeFilter

object FilterIsDate : DateTimeFilter.Values("is_date") {
    override val description = "Matches when it is one of the given dates."

    override val additionalInfo = listOf(
        "Dates as MM-DD (every year) or YYYY-MM-DD (one date only). A single value or a list.",
        "The date and time are worked out in the timezone set by dates.timezone in config.yml."
    )

    override fun compile(value: List<String>): DateTimeMatcher = DateTimeMatchers.dates(value)
}
