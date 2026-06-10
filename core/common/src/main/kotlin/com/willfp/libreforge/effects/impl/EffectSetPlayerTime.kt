package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSetPlayerTime : Effect<NoCompileData>("set_player_time") {
    override val description = "Overrides the time of day shown to the player on their client, without affecting the server."
    override val categories = setOf("player", "visual")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "time",
            "You must specify the time (ticks: 0=dawn, 6000=noon, 12000=dusk, 18000=midnight)!",
            description = "The time in ticks to display (0=dawn, 6000=noon, 12000=dusk, 18000=midnight).",
            type = ArgType.INT
        )
        optional(
            "relative",
            description = "If true, the time is applied relative to the world's current time instead of as a fixed value.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "reset",
            description = "If true, resets the player's time back to the server time.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        if (config.getBool("reset")) {
            player.resetPlayerTime()
            return true
        }

        val time = config.getInt("time").toLong()
        val relative = config.getBool("relative")

        player.setPlayerTime(time, relative)
        return true
    }
}
