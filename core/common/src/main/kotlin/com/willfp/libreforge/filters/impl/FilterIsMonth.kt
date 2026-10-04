package com.willfp.libreforge.filters.impl

import com.willfp.libreforge.dates.DateTimeMatcher
import com.willfp.libreforge.dates.DateTimeMatchers
import com.willfp.libreforge.filters.DateTimeFilter

object FilterIsMonth : DateTimeFilter.Values("is_month") {
    override val description = "Matches when it is one of the given months."

    override val additionalInfo = listOf(
        "Month names (december) or numbers (1-12). A single value or a list.",
        "The date and time are worked out in the timezone set by dates.timezone in config.yml."
    )

    override fun compile(value: List<String>): DateTimeMatcher = DateTimeMatchers.months(value)
}
