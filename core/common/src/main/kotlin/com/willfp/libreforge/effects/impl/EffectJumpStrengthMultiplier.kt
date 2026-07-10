package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectJumpStrengthMultiplier : AttributeEffect(
    "jump_strength_multiplier",
    Attribute.JUMP_STRENGTH,
    AttributeModifier.Operation.MULTIPLY_SCALAR_1
) {
    override val description = "Multiplies the player's jump strength, making them jump higher or lower."
    override val categories = setOf("movement", "player", "attribute")

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the jump strength multiplier!",
            description = "The jump strength multiplier to apply. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1 + %level% * 0.05"
        )
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("multiplier", entity as? Player) - 1
}
