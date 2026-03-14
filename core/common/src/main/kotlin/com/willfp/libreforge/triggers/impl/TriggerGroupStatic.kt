package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.util.evaluateExpressionOrNull
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerGroup
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Bukkit
import org.bukkit.FluidCollisionMode
import org.bukkit.entity.Player

object TriggerGroupStatic : TriggerGroup("static") {
    private val registry = mutableMapOf<Int, TriggerStatic>()
    private val dynamicRegistry = mutableMapOf<String, TriggerDynamicStatic>()
    private var tick = 0

    override fun create(value: String): Trigger? {
        val interval = value.toIntOrNull()

        if (interval == null) {
            return dynamicRegistry.getOrPut(value) { TriggerDynamicStatic(value) }
        }

        if (interval < 1) {
            return null
        }

        return registry.getOrPut(interval) { TriggerStatic(interval) }
    }

    override fun postRegister() {
        plugin.scheduler.runTimer(1, 1) {
            tick++

            for ((interval, trigger) in registry) {
                if (tick % interval == 0) {
                    for (player in Bukkit.getOnlinePlayers()) {
                        trigger.dispatchFor(player)
                    }
                }
            }

            for ((_, trigger) in dynamicRegistry) {
                for (player in Bukkit.getOnlinePlayers()) {
                    trigger.dispatchIfMet(player, tick)
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
    }

    private class TriggerDynamicStatic(
        private val expression: String
    ) : Trigger("static_expr_${expression.hashCode()}") {
        override val parameters = setOf(
            TriggerParameter.PLAYER,
            TriggerParameter.LOCATION,
            TriggerParameter.VICTIM,
            TriggerParameter.BLOCK,
            TriggerParameter.VELOCITY,
            TriggerParameter.ITEM
        )

        fun dispatchIfMet(player: Player, tick: Int) {
            val interval = evaluateExpressionOrNull(
                expression,
                placeholderContext(player = player)
            )?.toInt() ?: return

            if (interval < 1) {
                return
            }

            if (tick % interval != 0) {
                return
            }

            this.dispatchFor(player)
        }
    }

    private fun Trigger.dispatchFor(player: Player) {
        val block = if (Prerequisite.HAS_PAPER.isMet) {
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
