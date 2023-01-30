package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionStandingOnBlock : Condition("standing_on_block") {
    override val arguments = arguments {
        require("block", "You must specify the type of block!")
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        return player.world.getBlockAt(player.location.clone().add(0.0, -1.0, 0.0))
            .type.name.equals(config.getString("block"), ignoreCase = true)
    }
}
