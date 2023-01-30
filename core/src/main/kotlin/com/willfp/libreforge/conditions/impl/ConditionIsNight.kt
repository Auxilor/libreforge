package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionIsNight : Condition("is_night") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        val isNight = player.world.time in 12301..23849

        return isNight == (config.getBoolOrNull("is_night") ?: true)
    }
}
