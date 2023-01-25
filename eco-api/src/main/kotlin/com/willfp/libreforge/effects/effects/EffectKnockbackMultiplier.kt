package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.GenericAttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player

class EffectKnockbackMultiplier : GenericAttributeEffect(
    "knockback_multiplier",
    Attribute.GENERIC_ATTACK_KNOCKBACK,
    AttributeModifier.Operation.MULTIPLY_SCALAR_1
) {
    override val arguments = arguments {
        require("multiplier", "You must specify the knockback multiplier!")
    }

    override fun getValue(config: Config, player: Player) =
        config.getDoubleFromExpression("multiplier", player) - 1

}
