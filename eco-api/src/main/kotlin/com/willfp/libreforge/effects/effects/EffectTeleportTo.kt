package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.Bukkit
import org.bukkit.Location

class EffectTeleportTo : Effect(
    "teleport_to",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        val world = Bukkit.getWorld(config.getString("world")) ?: return
        val loc = Location(
            world,
            config.getDoubleFromExpression("x", data),
            config.getDoubleFromExpression("y", data),
            config.getDoubleFromExpression("z", data)
        )

        player.teleport(loc)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("world")) violations.add(
            ConfigViolation(
                "world",
                "You must specify the world to go to!"
            )
        )

        if (!config.has("x")) violations.add(
            ConfigViolation(
                "x",
                "You must specify the x coordinate!"
            )
        )

        if (!config.has("y")) violations.add(
            ConfigViolation(
                "y",
                "You must specify the y coordinate!"
            )
        )

        if (!config.has("z")) violations.add(
            ConfigViolation(
                "z",
                "You must specify the z coordinate!"
            )
        )

        return violations
    }
}
