package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.enumValueOfOrNull
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.GameMode

object EffectSetGamemode : Effect<NoCompileData>("set_gamemode") {
    override val description = "Sets the player's gamemode."
    override val categories = setOf("player")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "gamemode",
            "You must specify the gamemode!",
            description = "The gamemode to put the player into.",
            type = ArgType.STRING,
            enumClass = GameMode::class,
            example = "CREATIVE"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val gamemode = enumValueOfOrNull<GameMode>(
            config.getFormattedString("gamemode", data).uppercase()
        ) ?: return false

        player.gameMode = gamemode

        return true
    }
}
