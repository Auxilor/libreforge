package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.holidays.Holidays
import com.willfp.libreforge.triggers.TriggerData
import java.time.LocalDate

/**
 * `is_<holiday>`, generated for every holiday in holidays.yml.
 */
class FilterIsHoliday(
    private val holidayId: String
) : Filter<NoCompileData, Boolean>("is_$holidayId") {
    override val description = "Matches when it is (or is not) ${holidayId.replace('_', ' ')}."
    override val categories = setOf("holiday")
    override val valueType = ArgType.BOOLEAN
    override val additionalInfo = listOf(
        "The date is worked out in the timezone set by holidays.timezone in config.yml.",
        "Holidays are defined in holidays.yml."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val isOn = Holidays.getByID(holidayId)?.isOn(LocalDate.now(Holidays.zone)) ?: false
        return isOn == value
    }
}
