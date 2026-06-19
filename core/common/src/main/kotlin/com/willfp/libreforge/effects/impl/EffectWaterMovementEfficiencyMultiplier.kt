package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectWaterMovementEfficiencyMultiplier : AttributeEffect(
    "water_movement_efficiency_multiplier",
    Attribute.WATER_MOVEMENT_EFFICIENCY,
    AttributeModifier.Operation.MULTIPLY_SCALAR_1
) {
    override val description = "Multiplies the player's movement efficiency while in water."
    override val categories = setOf("movement", "player", "attribute")

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the movement efficiency multiplier!",
            description = "The multiplier to apply to water movement efficiency. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("multiplier", entity as? Player) - 1
}
