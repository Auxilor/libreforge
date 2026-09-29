package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.holidays.HolidayClock
import com.willfp.libreforge.holidays.HolidayEntry
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Bukkit
import java.time.LocalDate

/**
 * `<holiday>_start`, generated for every holiday and holiday period.
 */
class TriggerHolidayStart(
    private val holiday: HolidayEntry
) : Trigger("${holiday.id}_start") {
    override val description = "Fires for every online player at midnight when ${holiday.id.replace('_', ' ')} begins."

    override val categories = setOf("holiday")

    override val additionalInfo = listOf(
        "Midnight is in the timezone set by holidays.timezone in config.yml.",
        "Players who join later in the day are not triggered; use the is_${holiday.id} condition for that."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    override fun postRegister() {
        HolidayClock.onNewDay { dispatchIfStarting(it) }
    }

    private fun dispatchIfStarting(date: LocalDate) {
        if (!holiday.startsOn(date)) {
            return
        }

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

    internal companion object {
        val all = HolidayEntry.all.map { TriggerHolidayStart(it) }
    }
}
