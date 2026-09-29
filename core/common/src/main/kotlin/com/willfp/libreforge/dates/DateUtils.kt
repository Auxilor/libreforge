package com.willfp.libreforge.dates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit

/**
 * Fire this trigger for every online player, at their location.
 */
internal fun Trigger.dispatchForOnlinePlayers() {
    for (player in Bukkit.getOnlinePlayers()) {
        dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location
            )
        )
    }
}

/**
 * The strings at [key], which may be a single string or a list.
 */
internal fun Config.getStringOrStrings(key: String): List<String> =
    getStringsOrNull(key) ?: listOfNotNull(getStringOrNull(key))

/**
 * The strings at whichever of [keys] is set first, each of which may be a single string or a list.
 */
internal fun Config.getStringOrStrings(keys: List<String>): List<String> =
    keys.firstOrNull { has(it) }?.let { getStringOrStrings(it) } ?: emptyList()
