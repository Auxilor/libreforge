package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectMovementEfficiencyMultiplier : AttributeEffect(
    "movement_efficiency_multiplier",
    Attribute.MOVEMENT_EFFICIENCY,
    AttributeModifier.Operation.MULTIPLY_SCALAR_1
) {
    override val description = "Multiplies the player's movement efficiency, reducing the speed penalty from blocks like soul sand."
    override val categories = setOf("movement", "player")

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the movement efficiency multiplier!",
            description = "The movement efficiency multiplier to apply (e.g. 2 = double efficiency). Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("multiplier", entity as? Player) - 1
}
