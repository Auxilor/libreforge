package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.GenericAttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player

class EffectAttackSpeedMultiplier : GenericAttributeEffect(
    "attack_speed_multiplier",
    Attribute.GENERIC_ATTACK_SPEED,
    AttributeModifier.Operation.MULTIPLY_SCALAR_1
) {
    override val arguments = arguments {
        require("multiplier", "You must specify the attack speed multiplier!")
    }

    override fun getValue(config: Config, player: Player) =
        config.getDoubleFromExpression("multiplier", player) - 1
}
