package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionInGamemode : Condition<NoCompileData>("in_gamemode") {
    override val description = "Passes when the player is in the specified gamemode."
    override val categories = setOf("player")

    override val arguments = arguments {
        require(
            "gamemode",
            "You must specify the gamemode!",
            description = "The gamemode name (e.g. SURVIVAL, CREATIVE, ADVENTURE, SPECTATOR).",
            type = ArgType.STRING
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        return player.gameMode.name.equals(config.getString("gamemode"), ignoreCase = true)
    }
}
