package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Bukkit
import org.bukkit.Location

object EffectTeleportTo : Effect<NoCompileData>("teleport_to") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("world", "You must specify the world to go to!")
        require("x", "You must specify the x coordinate!")
        require("y", "You must specify the y coordinate!")
        require("z", "You must specify the z coordinate!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val world = Bukkit.getWorld(config.getString("world")) ?: return false
        val loc = Location(
            world,
            config.getDoubleFromExpression("x", data),
            config.getDoubleFromExpression("y", data),
            config.getDoubleFromExpression("z", data)
        )

        player.teleport(loc)

        return true
    }
}
