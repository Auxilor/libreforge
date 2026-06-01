package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.util.Vector

object EffectSetVictimVelocity : Effect<NoCompileData>("set_victim_velocity") {
    override val description = "Sets the victim entity's velocity to the specified x, y, z components."
    override val categories = setOf("movement", "combat")

    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require(
            "x",
            "You must specify the velocity x component!",
            description = "The X component of the velocity vector. Supports expressions.",
            type = ArgType.EXPRESSION
        )
        require(
            "y",
            "You must specify the velocity y component!",
            description = "The Y component of the velocity vector. Supports expressions.",
            type = ArgType.EXPRESSION
        )
        require(
            "z",
            "You must specify the velocity z component!",
            description = "The Z component of the velocity vector. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false

        plugin.scheduler.runLater(1) {
            victim.velocity = Vector(
                config.getDoubleFromExpression("x", data),
                config.getDoubleFromExpression("y", data),
                config.getDoubleFromExpression("z", data)
            )
        }

        return true
    }
}
