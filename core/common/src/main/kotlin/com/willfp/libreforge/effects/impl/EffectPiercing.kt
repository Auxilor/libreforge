package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.AbstractArrow

object EffectPiercing : Effect<NoCompileData>("piercing") {
    override val description = "Adds extra piercing levels to an arrow projectile, allowing it to pass through additional entities."
    override val categories = setOf("combat")

    override val arguments = arguments {
        require(
            "level",
            "You must specify the pierce level!",
            description = "The number of extra piercing levels to add to the arrow. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1 + %level% / 20"
        )
    }

    override val parameters = setOf(
        TriggerParameter.PROJECTILE
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val arrow = data.projectile as? AbstractArrow ?: return false

        arrow.pierceLevel += config.getIntFromExpression("level", data)

        return true
    }
}
