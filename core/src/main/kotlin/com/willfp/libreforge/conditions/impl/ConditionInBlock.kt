package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

object ConditionInBlock : Condition<NoCompileData>("in_block") {
    override val arguments = arguments {
        require("block", "You must specify the block!")
    }

    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        val world = player.world
        val head = world.getBlockAt(player.eyeLocation).type
        val feet = world.getBlockAt(player.eyeLocation.clone().subtract(0.0, 1.0, 0.0)).type
        val block = config.getString("block")
        return head.name.equals(block, ignoreCase = true) || feet.name.equals(block, ignoreCase = true)
    }
}
