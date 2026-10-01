package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.dates.Dates
import com.willfp.libreforge.seasons.Seasons
import java.time.LocalDate

/**
 * `is_<season>`, generated for every season in seasons.yml.
 */
class ConditionIsSeason(
    private val seasonId: String
) : Condition<NoCompileData>("is_$seasonId") {
    override val description = "Passes when it is ${seasonId.replace('_', ' ')}."

    override val categories = setOf("season")

    override val additionalInfo = listOf(
        "The date is worked out in the timezone set by dates.timezone in config.yml.",
        "Seasons are defined in seasons.yml."
    )

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        return Seasons.getByID(seasonId)?.isOn(LocalDate.now(Dates.zone)) ?: false
    }
}
