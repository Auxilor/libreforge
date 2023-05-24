package com.willfp.libreforge.triggers

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.map.listMap
import org.bukkit.entity.Player
import java.util.UUID

/*

Prevents multiple identical triggers from being triggered in the same tick.

 */

class DispatchedTriggerFactory(
    private val plugin: EcoPlugin
) {
    private val playerTriggers = listMap<UUID, Int>()

    fun create(player: Player, trigger: Trigger, data: TriggerData): DispatchedTrigger? {
        if (!trigger.isEnabled) {
            return null
        }

        val hash = (trigger.hashCode() shl 5) xor data.hashCode()
        if (hash in playerTriggers[player.uniqueId]) {
            return null
        }

        playerTriggers[player.uniqueId] += hash
        return DispatchedTrigger(player, trigger, data)
    }

    internal fun startTicking() {
        plugin.scheduler.runTimer(1, 1) {
            playerTriggers.clear()
        }
    }
}
