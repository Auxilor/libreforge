package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectPullToLocation : Effect<NoCompileData>("pull_to_location") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require("velocity", "You must specify the movement velocity!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val location = data.location ?: return false

        if (player.world != location.world) {
            return false
        }

        val vector = location.toVector().subtract(player.location.toVector()).normalize()
            .multiply(config.getDoubleFromExpression("velocity", data))

        player.velocity = vector

        return true
    }
}
