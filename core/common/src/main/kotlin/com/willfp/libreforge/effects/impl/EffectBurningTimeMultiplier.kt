package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectBurningTimeMultiplier : AttributeEffect(
    "burning_time_multiplier",
    Attribute.BURNING_TIME,
    AttributeModifier.Operation.MULTIPLY_SCALAR_1
) {
    override val description = "Multiplies the duration the player burns when on fire while the holder is active."
    override val categories = setOf("combat", "player", "attribute")

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the burning time multiplier!",
            description = "The burning time multiplier. Values below 1 reduce burn duration. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1 - %level% * 0.05"
        )
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("multiplier", entity as? Player) - 1
}
