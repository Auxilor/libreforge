package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Bukkit

/**
 * `<holiday>_start`, generated for every holiday in holidays.yml.
 */
class TriggerHolidayStart(
    holidayId: String
) : Trigger("${holidayId}_start") {
    override val description = "Fires for every online player at midnight when ${holidayId.replace('_', ' ')} begins."

    override val categories = setOf("holiday")

    override val additionalInfo = listOf(
        "Midnight is in the timezone set by holidays.timezone in config.yml.",
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

    internal fun dispatchForOnlinePlayers() {
        for (player in Bukkit.getOnlinePlayers()) {
            this.dispatch(
                player.toDispatcher(),
                TriggerData(
                    player = player,
                    location = player.location
                )
            )
        }
    }
}
