package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.dates.Dates
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.seasons.Seasons
import com.willfp.libreforge.triggers.TriggerData
import java.time.LocalDate

/**
 * `is_<season>`, generated for every season in seasons.yml.
 */
class FilterIsSeason(
    private val seasonId: String
) : Filter<NoCompileData, Boolean>("is_$seasonId") {
    override val description = "Matches when it is (or is not) ${seasonId.replace('_', ' ')}."
    override val categories = setOf("season")
    override val valueType = ArgType.BOOLEAN
    override val additionalInfo = listOf(
        "The date is worked out in the timezone set by dates.timezone in config.yml.",
        "Seasons are defined in seasons.yml."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val isOn = Seasons.getByID(seasonId)?.isOn(LocalDate.now(Dates.zone)) ?: false
        return isOn == value
    }
}
