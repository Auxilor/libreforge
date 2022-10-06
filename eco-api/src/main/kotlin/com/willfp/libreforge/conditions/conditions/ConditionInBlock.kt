package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

class ConditionInBlock : Condition("in_world") {
    override fun isConditionMet(player: Player, config: Config): Boolean {
        val world = player.world
        val head = world.getBlockAt(player.eyeLocation).type
        val feet = world.getBlockAt(player.eyeLocation.clone().subtract(0.0, 1.0, 0.0)).type
        val block = config.getString("block")
        return head.name.equals(block, ignoreCase = true) || feet.name.equals(block, ignoreCase = true)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("block")) violations.add(
            ConfigViolation(
                "block",
                "You must specify the block!"
            )
        )

        return violations
    }
}
