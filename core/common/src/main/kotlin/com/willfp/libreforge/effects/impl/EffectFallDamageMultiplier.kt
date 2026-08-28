package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectFallDamageMultiplier : AttributeEffect(
    "fall_damage_multiplier",
    Attribute.FALL_DAMAGE_MULTIPLIER,
    AttributeModifier.Operation.MULTIPLY_SCALAR_1
) {
    override val description = "Multiplies the fall damage the entity takes."
    override val categories = setOf("movement", "combat", "attribute")

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the fall damage multiplier!",
            description = "The fall damage multiplier to apply. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1 - %level% * 0.05"
        )
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("multiplier", entity as? Player) - 1
}
