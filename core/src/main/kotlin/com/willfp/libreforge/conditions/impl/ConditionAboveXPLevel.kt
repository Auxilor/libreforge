package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.updateEffects
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerExpChangeEvent


object ConditionAboveXPLevel : Condition<NoCompileData>("above_xp_level") {
    override val arguments = arguments {
        require("level", "You must specify the xp level!")
    }

    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        return player.level >= config.getIntFromExpression("level", player)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: PlayerExpChangeEvent) {
        event.player.updateEffects()
    }
}
