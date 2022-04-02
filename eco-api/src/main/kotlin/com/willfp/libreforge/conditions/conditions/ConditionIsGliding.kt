package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionIsGliding : Condition("is_gliding") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.isGliding == (config.getBoolOrNull("is_gliding") ?: true)
    }
}