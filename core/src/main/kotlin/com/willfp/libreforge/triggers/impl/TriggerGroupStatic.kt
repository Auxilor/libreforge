package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerGroup
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Bukkit
import org.bukkit.FluidCollisionMode
import org.bukkit.entity.Player
import java.lang.RuntimeException

object TriggerGroupStatic : TriggerGroup("static") {
    private val registry = mutableMapOf<Int, TriggerStatic>()
    private var tick = 0

    override fun create(value: String): Trigger? {
        val interval = value.toIntOrNull() ?: return null

        if (interval < 1) {
            return null
        }

        return registry.getOrPut(interval) { TriggerStatic(interval) }
    }

    override fun postRegister() {
        plugin.scheduler.runTimer(1, 1) {
            tick++

            if (registry.isEmpty()) {
                return@runTimer
            }

            for ((interval, trigger) in registry) {
                if (tick % interval == 0) {
                    for (player in Bukkit.getOnlinePlayers()) {
                        trigger.dispatch(player)
                    }
                }
            }
        }
    }

    private class TriggerStatic(interval: Int) : Trigger("static_$interval") {
        override val parameters = setOf(
            TriggerParameter.PLAYER,
            TriggerParameter.LOCATION,
            TriggerParameter.VICTIM,
            TriggerParameter.BLOCK,
            TriggerParameter.VELOCITY,
            TriggerParameter.ITEM
        )

        fun dispatch(player: Player) {
    
            val block = if (Prerequisite.HAS_PAPER.isMet && Prerequisite.HAS_1_20.isMet) {
                player.getTargetBlockExact(plugin.configYml.getInt("raytrace-distance"), FluidCollisionMode.NEVER)
            } else null

            this.dispatch(
                player.toDispatcher(),
                TriggerData(
                    player = player,
                    location = player.location,
                    victim = player,
                    block = block,
                    velocity = player.velocity,
                    item = player.inventory.itemInMainHand
                )
            )
        }
    }
}
