package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectSweepingDamageRatio : AttributeEffect(
    "sweeping_damage_ratio",
    Attribute.SWEEPING_DAMAGE_RATIO,
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val description = "Adds to the fraction of attack damage dealt by sweeping attacks."
    override val categories = setOf("combat", "attribute")

    override val arguments = arguments {
        require(
            "ratio",
            "You must specify the amount of ratio to add!",
            description = "The ratio to add, as a fraction between 0 and 1. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% * 0.1"
        )
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("ratio", entity as? Player)
}
