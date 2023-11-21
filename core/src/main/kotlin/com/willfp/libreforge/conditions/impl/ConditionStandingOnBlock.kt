package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.triggers.Dispatcher
import com.willfp.libreforge.triggers.get
import org.bukkit.entity.Player

object ConditionStandingOnBlock : Condition<NoCompileData>("standing_on_block") {
    override val arguments = arguments {
        require("block", "You must specify the type of block!")
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
