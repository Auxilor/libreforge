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

object EffectPullIn : Effect<NoCompileData>("pull_in") {
    override val description = "Pulls the victim toward the player with a specified velocity."
    override val categories = setOf("movement", "combat")

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require(
            "velocity",
            "You must specify the movement velocity!",
            description = "The strength of the pull force applied to the victim. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val victim = data.victim ?: return false

        val diff = player.location.toVector().clone().subtract(victim.location.toVector())
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
