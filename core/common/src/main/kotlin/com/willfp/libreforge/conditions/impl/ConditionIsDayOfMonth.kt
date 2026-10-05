package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.DateTimeCondition
import com.willfp.libreforge.dates.DateTimeMatcher
import com.willfp.libreforge.dates.DateTimeMatchers
import com.willfp.libreforge.dates.getStringOrStrings

object ConditionIsDayOfMonth : DateTimeCondition("is_day_of_month") {
    override val description = "Passes when it is one of the given days of the month."

    override val arguments = arguments {
        require(
            listOf("day", "days"),
            "You must specify the days!",
            description = "Days of the month (1-31). A single value (day) or a list (days).",
            type = ArgType.STRING_LIST,
            example = listOf("1", "15")
        )
    }

    override fun compile(config: Config): DateTimeMatcher =
        DateTimeMatchers.daysOfMonth(config.getStringOrStrings(listOf("day", "days")))
}
