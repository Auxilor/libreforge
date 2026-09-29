package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.holidays.Holidays
import java.time.LocalDate

/**
 * `is_<holiday>`, generated for every holiday in holidays.yml.
 */
class ConditionIsHoliday(
    private val holidayId: String
) : Condition<NoCompileData>("is_$holidayId") {
    override val description = "Passes when it is ${holidayId.replace('_', ' ')}."

    override val categories = setOf("holiday")

    override val additionalInfo = listOf(
        "The date is worked out in the timezone set by holidays.timezone in config.yml.",
        "Holidays are defined in holidays.yml."
    )

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        return Holidays.getByID(holidayId)?.isOn(LocalDate.now(Holidays.zone)) ?: false
    }
}
