package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.WeatherType

object EffectSetPlayerWeather : Effect<NoCompileData>("set_player_weather") {
    override val description = "Overrides the weather shown to the player on their client, without affecting the server."
    override val categories = setOf("player", "visual")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "weather",
            "You must specify the weather type (clear or downfall)!",
            description = "The client weather to display.",
            type = ArgType.STRING,
            choices = listOf("clear", "downfall")
        )
        optional(
            "reset",
            description = "If true, resets the player's weather back to the server weather.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        if (config.getBool("reset")) {
            player.resetPlayerWeather()
            return true
        }

        val weatherType = when (config.getString("weather").lowercase()) {
            "clear" -> WeatherType.CLEAR
            "downfall", "rain", "storm" -> WeatherType.DOWNFALL
            else -> return false
        }

        player.setPlayerWeather(weatherType)
        return true
    }
}
