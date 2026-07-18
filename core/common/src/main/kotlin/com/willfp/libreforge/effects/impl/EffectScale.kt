package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectScale : AttributeEffect(
    "scale",
    Attribute.SCALE,
    AttributeModifier.Operation.MULTIPLY_SCALAR_1
) {
    override val description = "Scales the entity's size by a given multiplier."
    override val categories = setOf("entity", "attribute")

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the scale multiplier!",
            description = "The scale multiplier to apply to the entity's size. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1 + %level% * 0.05"
        )
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("multiplier", entity as? Player) - 1
}
