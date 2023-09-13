package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

object ConditionStandingOnBlock : Condition<NoCompileData>("standing_on_block") {
    override val arguments = arguments {
        require("block", "You must specify the type of block!")
    }

    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        val tests = setOf(
            player.location.block,
            player.location.clone().add(0.0, -1.0, 0.0).block
        )

        return tests.any { it.type.name.equals(config.getString("block"), ignoreCase = true) }
    }
}
