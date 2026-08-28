package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectFrictionMultiplier : AttributeEffect(
    "friction_multiplier",
    "friction_modifier",
    AttributeModifier.Operation.MULTIPLY_SCALAR_1
) {
    override val description = "Multiplies the friction the entity experiences while moving."
    override val categories = setOf("movement", "attribute")

    override val additionalInfo = listOf("Requires Minecraft 26.2 or newer.")

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the friction multiplier!",
            description = "The friction multiplier to apply. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1 - %level% * 0.05"
        )
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("multiplier", entity as? Player) - 1
}
