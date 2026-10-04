package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.DateTimeCondition
import com.willfp.libreforge.dates.DateTimeMatcher
import com.willfp.libreforge.dates.DateTimeMatchers
import com.willfp.libreforge.dates.getStringOrStrings

object ConditionIsHour : DateTimeCondition("is_hour") {
    override val description = "Passes when it is one of the given hours."

    override val arguments = arguments {
        require(
            listOf("hour", "hours"),
            "You must specify the hours!",
            description = "Hours of the day (0-23). A single value (hour) or a list (hours).",
            type = ArgType.STRING_LIST,
            example = listOf("18", "19")
        )
    }

    override fun compile(config: Config): DateTimeMatcher =
        DateTimeMatchers.hours(config.getStringOrStrings(listOf("hour", "hours")))
}
