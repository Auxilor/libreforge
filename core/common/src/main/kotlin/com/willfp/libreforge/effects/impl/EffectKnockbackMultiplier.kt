package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectKnockbackMultiplier : AttributeEffect(
    "knockback_multiplier",
    Attribute.ATTACK_KNOCKBACK,
    AttributeModifier.Operation.MULTIPLY_SCALAR_1
) {
    override val description = "Multiplies the knockback dealt by the player when attacking."
    override val categories = setOf("combat", "player", "attribute")

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the knockback multiplier!",
            description = "The knockback multiplier to apply. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("multiplier", entity as? Player) - 1
}
