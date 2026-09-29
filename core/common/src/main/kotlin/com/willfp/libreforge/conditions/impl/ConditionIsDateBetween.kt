package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.DateTimeCondition
import com.willfp.libreforge.dates.DateTimeMatcher
import com.willfp.libreforge.dates.DateTimeMatchers

object ConditionIsDateBetween : DateTimeCondition("is_date_between") {
    override val description = "Passes when the date is between from and to (inclusive)."

    override val additionalInfo = listOf(
        "An MM-DD range wraps across new year when to is earlier than from.",
        "The date and time are worked out in the timezone set by dates.timezone in config.yml."
    )

    override val arguments = arguments {
        require(
            "from",
            "You must specify the date to start from!",
            description = "The first date. Dates as MM-DD (every year) or YYYY-MM-DD (one date only); from and to must use the same format.",
            type = ArgType.STRING,
            example = "12-20"
        )

        require(
            "to",
            "You must specify the date to end at!",
            description = "The last date. Dates as MM-DD (every year) or YYYY-MM-DD (one date only); from and to must use the same format.",
            type = ArgType.STRING,
            example = "01-05"
        )
    }

    override fun compile(config: Config): DateTimeMatcher =
        DateTimeMatchers.dateBetween(config.getString("from"), config.getString("to"))
}
