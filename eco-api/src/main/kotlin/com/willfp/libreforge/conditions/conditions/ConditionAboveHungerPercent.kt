package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.FoodLevelChangeEvent


class ConditionAboveHungerPercent : Condition("above_hunger_percent") {
    @EventHandler(
        priority = EventPriority.MONITOR,
        ignoreCancelled = true
    )
    fun handle(event: FoodLevelChangeEvent) {
        val player = event.entity

        if (player !is Player) {
            return
        }

        player.updateEffects(noRescan = true)
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return (player.foodLevel / 20) * 100 >= config.getDoubleFromExpression("percent", player)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("percent")) violations.add(
            ConfigViolation(
                "percent",
                "You must specify the hunger percentage!"
            )
        )

        return violations
    }
}