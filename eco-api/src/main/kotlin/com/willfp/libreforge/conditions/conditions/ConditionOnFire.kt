package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent

class ConditionOnFire : Condition("on_fire") {
    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: EntityDamageEvent) {
        val player = event.entity as? Player ?: return

        if (event.cause != EntityDamageEvent.DamageCause.FIRE
            && event.cause != EntityDamageEvent.DamageCause.FIRE_TICK
            && event.cause != EntityDamageEvent.DamageCause.HOT_FLOOR
            && event.cause != EntityDamageEvent.DamageCause.LAVA
        ) {
            return
        }

        player.updateEffects()
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return (player.fireTicks > 0) == config.getBool("on_fire")
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getBoolOrNull("on_fire")
            ?: violations.add(
                ConfigViolation(
                    "on_fire",
                    "You must specify if the player must be on fire or not!"
                )
            )

        return violations
    }
}