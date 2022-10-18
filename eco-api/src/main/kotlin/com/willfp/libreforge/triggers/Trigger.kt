package com.willfp.libreforge.triggers

import com.willfp.libreforge.Holder
import com.willfp.libreforge.LibReforgePlugin
import com.willfp.libreforge.effects.inRunOrder
import com.willfp.libreforge.events.EffectPreInvokeEvent
import com.willfp.libreforge.events.TriggerPreProcessEvent
import com.willfp.libreforge.events.TriggerProcessEvent
import com.willfp.libreforge.getHolders
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.Objects

abstract class Trigger(
    val id: String,
    val parameters: Collection<TriggerParameter>
) : Listener {
    protected val plugin = LibReforgePlugin.instance

    init {
        postInit()
    }

    private fun postInit() {
        Triggers.addNewTrigger(this)
    }

    protected fun processTrigger(player: Player, data: TriggerData, forceHolders: Iterable<Holder>? = null) {
        processTrigger(player, data, 1.0, forceHolders = forceHolders)
    }

    protected fun processTrigger(
        player: Player,
        data: TriggerData,
        value: Double,
        forceHolders: Iterable<Holder>? = null
    ) {
        val preProcessEvent = TriggerPreProcessEvent(player, this, data, value)
        Bukkit.getPluginManager().callEvent(preProcessEvent)

        if (preProcessEvent.isCancelled) {
            return
        }

        for (holder in forceHolders ?: player.getHolders()) {
            var areMet = true
            for (condition in holder.conditions) {
                if (!condition.isMet(player)) {
                    areMet = false
                }
            }

            if (!areMet) {
                continue
            }

            val event = TriggerProcessEvent(player, holder, this, data, value)
            Bukkit.getPluginManager().callEvent(event)

            if (!event.isCancelled) {
                for (effect in holder.effects.inRunOrder()) {
                    val preInvoke = EffectPreInvokeEvent(player, holder, this, effect.effect, value)
                    Bukkit.getPluginManager().callEvent(preInvoke)

                    if (!preInvoke.isCancelled) {
                        effect(InvocationData(player, data, holder, this, effect.compileData, value))
                    }
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Trigger) {
            return false
        }

        return this.id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}
