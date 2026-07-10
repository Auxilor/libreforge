package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectExplosionKnockbackResistanceMultiplier : AttributeEffect("explosion_knockback_resistance_multiplier",
    Attribute.EXPLOSION_KNOCKBACK_RESISTANCE,
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val description = "Adds a flat value to the player's explosion knockback resistance attribute."
    override val categories = setOf("combat", "player", "attribute")

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the explosion knockback resistance multiplier!",
            description = "The value to add to the explosion knockback resistance attribute. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% * 0.1"
        )
    }

    override fun getValue(config: Config, entity: LivingEntity): Double {
        val multiplier = config.getDoubleFromExpression("multiplier", entity as? Player)
        val value = multiplier - 1.0

        return value
    }
}