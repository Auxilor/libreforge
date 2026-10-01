package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.DateTimeCondition
import com.willfp.libreforge.dates.DateTimeMatcher
import com.willfp.libreforge.dates.DateTimeMatchers

object ConditionIsHourBetween : DateTimeCondition("is_hour_between") {
    override val description = "Passes when the hour is between from and to (inclusive)."

    override val additionalInfo = listOf(
        "Wraps across midnight when to is earlier than from.",
        "The date and time are worked out in the timezone set by dates.timezone in config.yml."
    )

    override val arguments = arguments {
        require(
            "from",
            "You must specify the hour to start from!",
            description = "The first hour. Hours of the day (0-23); to includes the whole of that hour.",
            type = ArgType.STRING,
            example = "22"
        )

        require(
            "to",
            "You must specify the hour to end at!",
            description = "The last hour. Hours of the day (0-23); to includes the whole of that hour.",
            type = ArgType.STRING,
            example = "4"
        )
    }

    override fun compile(config: Config): DateTimeMatcher =
        DateTimeMatchers.hourBetween(config.getString("from"), config.getString("to"))
}
