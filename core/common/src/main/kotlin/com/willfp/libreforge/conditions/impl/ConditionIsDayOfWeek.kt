package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.DateTimeCondition
import com.willfp.libreforge.dates.DateTimeMatcher
import com.willfp.libreforge.dates.DateTimeMatchers
import com.willfp.libreforge.dates.getStringOrStrings

object ConditionIsDayOfWeek : DateTimeCondition("is_day_of_week") {
    override val description = "Passes when it is one of the given days of the week."

    override val arguments = arguments {
        require(
            listOf("day", "days"),
            "You must specify the days!",
            description = "Day names (friday) or numbers from 1 (Monday) to 7 (Sunday). A single value (day) or a list (days).",
            type = ArgType.STRING_LIST,
            example = listOf("saturday", "sunday")
        )
    }

    override fun compile(config: Config): DateTimeMatcher =
        DateTimeMatchers.daysOfWeek(config.getStringOrStrings(listOf("day", "days")))
}
