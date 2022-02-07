package com.willfp.libreforge.triggers

import com.willfp.libreforge.Holder
import com.willfp.libreforge.LibReforgePlugin
import com.willfp.libreforge.effects.CompileData
import com.willfp.libreforge.getHolders
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
        for (holder in forceHolders ?: player.getHolders()) {
            var areMet = true
            for ((condition, config) in holder.conditions) {
                if (!condition.isConditionMet(player, config)) {
                    areMet = false
                }
            }

            if (!areMet) {
                continue
            }

            for (effect in holder.effects) {
                effect(InvocationData(player, data, holder, this, effect.compileData))
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

data class InvocationData(
    val player: Player,
    val data: TriggerData,
    val holder: Holder,
    val trigger: Trigger,
    val compileData: CompileData?
)
