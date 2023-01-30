package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.TestableEntity
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.effects.CompileData
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.spigotmc.event.entity.EntityDismountEvent
import org.spigotmc.event.entity.EntityMountEvent

class ConditionRidingEntity : Condition("riding_entity") {
    override val arguments = arguments {
        require("entities", "You must specify the list of allowed entities!")
    }

    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: EntityDismountEvent) {
        val player = event.entity as? Player ?: return

        player.updateEffects(noRescan = true)
    }

    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: EntityMountEvent) {
        val player = event.entity as? Player ?: return

        player.updateEffects(noRescan = true)
    }

    override fun isConditionMet(player: Player, config: Config, data: CompileData?): Boolean {
        val compileData = data as? RidingEntityCompileData ?: return true

        return compileData.isMet(player.vehicle)
    }

    override fun makeCompileData(config: Config, context: ViolationContext): CompileData {
        return RidingEntityCompileData(config.getStrings("entities").map {
            Entities.lookup(it)
        })
    }

    private class RidingEntityCompileData(
        private val entities: Iterable<TestableEntity>
    ) : CompileData {
        fun isMet(entity: Entity?): Boolean {
            val list = entities.toList()

            if (list.isEmpty()) {
                return true
            }

            return list.any { it.matches(entity) }
        }
    }
}
