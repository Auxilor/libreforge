package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Bukkit

object EffectSetWeather : Effect<NoCompileData>("set_weather") {
    override val description = "Sets the weather in the world on the server, affecting every player in it."
    override val categories = setOf("world")

    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require(
            "weather",
            "You must specify the weather type (clear, rain or thunder)!",
            description = "The weather to set on the server.",
            type = ArgType.STRING,
            choices = listOf("clear", "rain", "thunder")
        )
        optional(
            "duration",
            description = "How long the weather lasts, in ticks. Defaults to the server's own choice. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "6000"
        )
        optional(
            "world",
            description = "The name of the world to change. Defaults to the world the effect was triggered in.",
            type = ArgType.STRING,
            example = "world_nether"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val worldName = config.getFormattedString("world", data)

        val world = if (worldName.isEmpty()) {
            data.location?.world ?: data.player?.world
        } else {
            Bukkit.getWorld(worldName)
        } ?: return false

        when (config.getFormattedString("weather", data).lowercase()) {
            "clear", "sun" -> {
                world.setStorm(false)
                world.isThundering = false
            }

            "rain", "downfall", "storm" -> {
                world.setStorm(true)
                world.isThundering = false
            }

            "thunder" -> {
                world.setStorm(true)
                world.isThundering = true
            }

            else -> return false
        }

        if (config.has("duration")) {
            val duration = config.getIntFromExpression("duration", data)

            world.weatherDuration = duration
            world.thunderDuration = duration
        }

        return true
    }
}
