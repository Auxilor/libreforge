package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.holidays.HolidayEntry
import com.willfp.libreforge.holidays.Holidays
import java.time.LocalDate

/**
 * `is_<holiday>`, generated for every holiday and holiday period.
 */
class ConditionIsHoliday(
    private val holiday: HolidayEntry
) : Condition<NoCompileData>("is_${holiday.id}") {
    override val description = "Passes when it is ${holiday.id.replace('_', ' ')}."

    override val categories = setOf("holiday")

    override val additionalInfo = listOf(
        "The date is worked out in the timezone set by holidays.timezone in config.yml."
    )

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        return holiday.isOn(LocalDate.now(Holidays.zone))
    }
}
