package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionInWorld : Condition("in_world") {
    override val arguments = arguments {
        require("world", "You must specify the world name!")
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.world.name.equals(config.getString("world"), ignoreCase = true)
    }
}
