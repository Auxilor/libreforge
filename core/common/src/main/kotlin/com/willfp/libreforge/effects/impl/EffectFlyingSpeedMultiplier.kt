package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectFlyingSpeedMultiplier : AttributeEffect(
    "flying_speed_multiplier",
    Attribute.FLYING_SPEED,
    AttributeModifier.Operation.MULTIPLY_SCALAR_1
) {
    override val description = "Multiplies the flying speed of a flying entity."
    override val categories = setOf("movement", "entity", "attribute")

    override val additionalInfo = listOf("Only affects entities that fly naturally, such as bees. It does not change player creative flight speed.")

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the flying speed multiplier!",
            description = "The flying speed multiplier to apply. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1 + %level% * 0.05"
        )
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("multiplier", entity as? Player) - 1
}
