package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionStandingOnBlock : Condition<NoCompileData>("standing_on_block") {
    override val description = "Passes when the player is standing on or inside the specified block type."
    override val categories = setOf("world")

    override val arguments = arguments {
        require(
            "block",
            "You must specify the type of block!",
            description = "The block type (material name) the player must be standing on.",
            type = ArgType.BLOCK
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        val tests = setOf(
            player.location.block,
            player.location.clone().add(0.0, -1.0, 0.0).block
        )

        return tests.any { it.type.name.equals(config.getString("block"), ignoreCase = true) }
    }
}
