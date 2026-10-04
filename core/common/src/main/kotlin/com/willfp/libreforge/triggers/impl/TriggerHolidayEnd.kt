package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

/**
 * `<holiday>_end`, generated for every holiday in holidays.yml and fired by [com.willfp.libreforge.holidays.Holidays].
 */
class TriggerHolidayEnd(
    holidayId: String
) : Trigger("${holidayId}_end") {
    override val description = "Fires for every online player in the last minute (23:59) of ${holidayId.replace('_', ' ')}."

    override val categories = setOf("holiday")

    override val additionalInfo = listOf(
        "23:59 is in the timezone set by dates.timezone in config.yml.",
        "Only players online at that moment are triggered.",
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
