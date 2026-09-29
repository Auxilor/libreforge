package com.willfp.libreforge.filters.impl

import com.willfp.libreforge.dates.DateTimeMatcher
import com.willfp.libreforge.dates.DateTimeMatchers
import com.willfp.libreforge.filters.DateTimeFilter

object FilterIsDateBetween : DateTimeFilter.Between("is_date_between") {
    override val description = "Matches when the date is between from and to (inclusive)."

    override val additionalInfo = listOf(
        "Set from and to beneath the filter, e.g. is_date_between: { from: \"12-20\", to: \"01-05\" }.",
        "Dates as MM-DD (every year) or YYYY-MM-DD (one date only); from and to must use the same format.",
        "An MM-DD range wraps across new year when to is earlier than from.",
        "The date and time are worked out in the timezone set by dates.timezone in config.yml."
    )

    override fun compile(from: String, to: String): DateTimeMatcher = DateTimeMatchers.dateBetween(from, to)
}
