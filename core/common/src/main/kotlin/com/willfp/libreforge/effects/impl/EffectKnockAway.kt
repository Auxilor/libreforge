package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.util.Vector

object EffectKnockAway : Effect<NoCompileData>("knock_away") {
    override val description = "Launches the victim away from the player with a specified velocity."
    override val categories = setOf("combat", "movement")

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require(
            "velocity",
            "You must specify the movement velocity!",
            description = "The speed at which the victim is knocked away from the player. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1.5"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val victim = data.victim ?: return false

        val diff = victim.location.toVector().clone().subtract(player.location.toVector())
        val direction = if (diff.lengthSquared() < 1e-6) {
            Vector(0.0, 0.0, 0.0)
        } else {
            diff.normalize()
        }
        val vector = direction.multiply(config.getDoubleFromExpression("velocity", data))

        victim.velocity = vector

        return true
    }
}
