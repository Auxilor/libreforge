package com.willfp.libreforge.triggers.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.libreforge.LibReforgePlugin
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerGroup
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class TriggerStatic(interval: Int) : Trigger(
    "static_$interval", listOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.VICTIM,
        TriggerParameter.BLOCK,
        TriggerParameter.VELOCITY,
        TriggerParameter.ITEM
    )
) {
    private fun invoke(player: Player) {
        val block = if (Prerequisite.HAS_PAPER.isMet) {
            player.getTargetBlock(plugin.configYml.getDoubleFromExpression("raytrace-distance").toInt())
        } else null

        this.processTrigger(
            player,
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

    companion object {
        private val intervals = mutableMapOf<Int, TriggerStatic>()

        internal fun registerGroup() {
            Triggers.addNewTriggerGroup(
                object : TriggerGroup(
                    "static"
                ) {
                    override fun create(value: String): Trigger? {
                        val interval = value.toIntOrNull() ?: return null
                        return intervals.getOrPut(interval) {
                            TriggerStatic(interval)
                        }
                    }
                }
            )
        }

        fun beginTiming(plugin: LibReforgePlugin) {
            var tick = 0

            plugin.scheduler.runTimer(1, 1) {
                tick++
                for ((interval, trigger) in intervals) {
                    if (tick % interval == 0) {
                        for (player in Bukkit.getOnlinePlayers()) {
                            trigger.invoke(player)
                        }
                    }
                }
            }
        }
    }
}
