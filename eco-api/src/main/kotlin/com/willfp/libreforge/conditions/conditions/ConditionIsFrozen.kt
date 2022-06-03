package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionIsFrozen : Condition("is_frozen") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.isFrozen
    }
}
