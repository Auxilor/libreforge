package com.willfp.libreforge.triggers

import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.Holder
import com.willfp.libreforge.LibReforge
import com.willfp.libreforge.events.EffectActivateEvent
import com.willfp.libreforge.getHolders
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.*

abstract class Trigger(
    val id: String
) : Listener {
    protected val plugin = LibReforge.plugin

    init {
        postInit()
    }

    private fun postInit() {
        Triggers.addNewTrigger(this)
    }

    protected fun processTrigger(player: Player, data: TriggerData, forceHolders: Iterable<Holder>? = null) {
        for (holder in forceHolders ?: player.getHolders()) {
            for ((effect, config, filter, triggers) in holder.effects) {
                if (NumberUtils.randFloat(0.0, 100.0) > (config.getDoubleOrNull("chance") ?: 100.0)) {
                    continue
                }

                if (!triggers.contains(this)) {
                    continue
                }

                if (!filter.matches(data)) {
                    continue
                }

                val activateEvent = EffectActivateEvent(player, holder, effect)
                this.plugin.server.pluginManager.callEvent(activateEvent)

                if (!activateEvent.isCancelled) {
                    effect.handle(data, config)
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
