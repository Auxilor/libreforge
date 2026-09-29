package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.holidays.HolidayEntry
import com.willfp.libreforge.holidays.Holidays
import com.willfp.libreforge.triggers.TriggerData
import java.time.LocalDate

/**
 * `is_<holiday>`, generated for every holiday and holiday period.
 */
class FilterIsHoliday(
    private val holiday: HolidayEntry
) : Filter<NoCompileData, Boolean>("is_${holiday.id}") {
    override val description = "Matches when it is (or is not) ${holiday.id.replace('_', ' ')}."
    override val categories = setOf("holiday")
    override val valueType = ArgType.BOOLEAN
    override val additionalInfo = listOf(
        "The date is worked out in the timezone set by holidays.timezone in config.yml."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        return holiday.isOn(LocalDate.now(Holidays.zone)) == value
    }
}
