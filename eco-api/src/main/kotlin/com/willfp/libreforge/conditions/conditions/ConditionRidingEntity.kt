package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.filters.containsIgnoreCase
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.spigotmc.event.entity.EntityDismountEvent
import org.spigotmc.event.entity.EntityMountEvent

class ConditionRidingEntity : Condition("riding_entity") {
    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: EntityDismountEvent) {
        val player = event.entity as? Player ?: return

        player.updateEffects()
    }

    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: EntityMountEvent) {
        val player = event.entity as? Player ?: return

        player.updateEffects()
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return config.getStrings("entities")
            .containsIgnoreCase(player.vehicle?.type?.name ?: return false)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getStringsOrNull("entities")
            ?: violations.add(
                ConfigViolation(
                    "entities",
                    "You must specify the entity list!"
                )
            )

        return violations
    }
}