package com.willfp.libreforge.triggers

import com.willfp.libreforge.activeEffects
import com.willfp.libreforge.triggers.event.TriggerDispatchEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

abstract class Trigger(
    val id: String
) {
    /**
     * The TriggerData parameters that are sent.
     */
    abstract val parameters: Collection<TriggerParameter>

    /**
     * Dispatch the trigger.
     */
    protected fun dispatch(
        player: Player,
        data: TriggerData
    ) {
        val dispatchEvent = TriggerDispatchEvent(player, this, data)
        Bukkit.getPluginManager().callEvent(dispatchEvent)
        if (dispatchEvent.isCancelled) {
            return
        }

        for (group in player.activeEffects) {
            group.trigger(player, this, data)
        }
    }
}
