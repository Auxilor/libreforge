package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.DateTimeCondition
import com.willfp.libreforge.dates.DateTimeMatcher
import com.willfp.libreforge.dates.DateTimeMatchers
import com.willfp.libreforge.dates.getStringOrStrings

object ConditionIsDate : DateTimeCondition("is_date") {
    override val description = "Passes when it is one of the given dates."

    override val arguments = arguments {
        require(
            listOf("date", "dates"),
            "You must specify the dates!",
            description = "Dates as MM-DD (every year) or YYYY-MM-DD (one date only). A single value (date) or a list (dates).",
            type = ArgType.STRING_LIST,
            example = listOf("12-25", "2026-10-31")
        )
    }

    override fun compile(config: Config): DateTimeMatcher =
        DateTimeMatchers.dates(config.getStringOrStrings(listOf("date", "dates")))
}
