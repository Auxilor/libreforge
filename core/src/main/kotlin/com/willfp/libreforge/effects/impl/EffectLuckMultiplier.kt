package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.GenericAttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player

class EffectLuckMultiplier : GenericAttributeEffect(
    "luck_multiplier",
    Attribute.GENERIC_LUCK,
    AttributeModifier.Operation.MULTIPLY_SCALAR_1
) {
    override val arguments = arguments {
        require("multiplier", "You must specify the luck multiplier!")
    }

    override fun getValue(config: Config, player: Player) =
        config.getDoubleFromExpression("multiplier", player) - 1
}
