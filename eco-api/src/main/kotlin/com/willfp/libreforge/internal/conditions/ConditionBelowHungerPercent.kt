package com.willfp.libreforge.internal.conditions

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.api.conditions.Condition
import com.willfp.libreforge.api.effects.ConfigViolation
import com.willfp.libreforge.api.provider.LibReforgeProviders
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.FoodLevelChangeEvent


class ConditionBelowHungerPercent : Condition("below_hunger_percent") {
    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: FoodLevelChangeEvent) {
        val player = event.entity

        if (player !is Player) {
            return
        }

        LibReforgeProviders.updateEffects(player)
    }

    override fun isConditionMet(player: Player, config: JSONConfig): Boolean {
        return (player.foodLevel / 20) * 100 <= config.getDouble("percent")
    }

    override fun validateConfig(config: JSONConfig): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getDoubleOrNull("percent")
            ?: violations.add(
                ConfigViolation(
                    "percent",
                    "You must specify the hunger percentage!"
                )
            )

        return violations
    }
}