package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.FoodLevelChangeEvent


object ConditionBelowHungerPercent : Condition<NoCompileData>("below_hunger_percent") {
    override val arguments = arguments {
        require("percent", "You must specify the hunger percentage!")
    }

    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        return player.foodLevel / 20 <= config.getDoubleFromExpression("percent", player) / 100
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: FoodLevelChangeEvent) {
        val player = event.entity as? Player ?: return
        player.updateEffects()
    }
}
