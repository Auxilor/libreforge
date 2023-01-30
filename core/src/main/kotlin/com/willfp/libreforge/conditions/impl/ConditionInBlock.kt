package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionInBlock : Condition("in_block") {
    override val arguments = arguments {
        require("block", "You must specify the block!")
    }

    override fun isConditionMet(player: Player, config: Config): Boolean {
        val world = player.world
        val head = world.getBlockAt(player.eyeLocation).type
        val feet = world.getBlockAt(player.eyeLocation.clone().subtract(0.0, 1.0, 0.0)).type
        val block = config.getString("block")
        return head.name.equals(block, ignoreCase = true) || feet.name.equals(block, ignoreCase = true)
    }
}
