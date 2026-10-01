package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.DateTimeCondition
import com.willfp.libreforge.dates.DateTimeMatcher
import com.willfp.libreforge.dates.DateTimeMatchers

object ConditionIsTimeBetween : DateTimeCondition("is_time_between") {
    override val description = "Passes when the time is between from and to (inclusive)."

    override val additionalInfo = listOf(
        "Wraps across midnight when to is earlier than from.",
        "The date and time are worked out in the timezone set by dates.timezone in config.yml."
    )

    override val arguments = arguments {
        require(
            "from",
            "You must specify the time to start from!",
            description = "The first time. Times as HH:MM (24 hour); to includes the whole of that minute.",
            type = ArgType.STRING,
            example = "21:30"
        )

        require(
            "to",
            "You must specify the time to end at!",
            description = "The last time. Times as HH:MM (24 hour); to includes the whole of that minute.",
            type = ArgType.STRING,
            example = "06:00"
        )
    }

    override fun compile(config: Config): DateTimeMatcher =
        DateTimeMatchers.timeBetween(config.getString("from"), config.getString("to"))
}
