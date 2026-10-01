package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

/**
 * `<holiday>_start`, generated for every holiday in holidays.yml and fired by [com.willfp.libreforge.holidays.Holidays].
 */
class TriggerHolidayStart(
    holidayId: String
) : Trigger("${holidayId}_start") {
    override val description = "Fires for every online player at midnight when ${holidayId.replace('_', ' ')} begins."

    override val categories = setOf("holiday")

    override val additionalInfo = listOf(
        "Midnight is in the timezone set by dates.timezone in config.yml.",
        "Players who join later in the day are not triggered; use the is_$holidayId condition for that.",
        "Holidays are defined in holidays.yml."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )
}
