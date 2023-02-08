package com.willfp.libreforge.triggers

import com.willfp.libreforge.Holder
import com.willfp.libreforge.activeEffects
import com.willfp.libreforge.getActiveEffects
import com.willfp.libreforge.triggers.event.TriggerDispatchEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

abstract class Trigger(
    val id: String
) {
    /**
     * The TriggerData parameters that are sent.
     */
    abstract val parameters: Set<TriggerParameter>

    /**
     * Dispatch the trigger.
     */
    protected fun dispatch(
        player: Player,
        data: TriggerData,
        forceHolders: Collection<Holder>? = null
    ) {
        val dispatch = DispatchedTrigger(player, this, data)

        val dispatchEvent = TriggerDispatchEvent(player, dispatch)
        Bukkit.getPluginManager().callEvent(dispatchEvent)
        if (dispatchEvent.isCancelled) {
            return
        }

        val effects = forceHolders?.getActiveEffects(player) ?: player.activeEffects

        for (block in effects) {
            block.tryTrigger(dispatch)
        }
    }
}
