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

object EffectSetTime : Effect<NoCompileData>("set_time") {
    override val description = "Sets the time of day in the world on the server, affecting every player in it."
    override val categories = setOf("world")

    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require(
            "time",
            "You must specify the time (ticks: 0=dawn, 6000=noon, 12000=dusk, 18000=midnight)!",
            description = "The time in ticks to set (0=dawn, 6000=noon, 12000=dusk, 18000=midnight). Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "6000"
        )
        optional(
            "relative",
            description = "If true, the time is added to the world's current time instead of replacing it.",
            type = ArgType.BOOLEAN,
            default = "false"
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

        val time = config.getIntFromExpression("time", data).toLong()

        world.time = if (config.getBool("relative")) world.time + time else time

        return true
    }
}
