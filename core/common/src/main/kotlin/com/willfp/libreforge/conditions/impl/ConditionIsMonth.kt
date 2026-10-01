package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.DateTimeCondition
import com.willfp.libreforge.dates.DateTimeMatcher
import com.willfp.libreforge.dates.DateTimeMatchers
import com.willfp.libreforge.dates.getStringOrStrings

object ConditionIsMonth : DateTimeCondition("is_month") {
    override val description = "Passes when it is one of the given months."

    override val arguments = arguments {
        require(
            listOf("month", "months"),
            "You must specify the months!",
            description = "Month names (december) or numbers (1-12). A single value (month) or a list (months).",
            type = ArgType.STRING_LIST,
            example = listOf("december", "january")
        )
    }

    override fun compile(config: Config): DateTimeMatcher =
        DateTimeMatchers.months(config.getStringOrStrings(listOf("month", "months")))
}
