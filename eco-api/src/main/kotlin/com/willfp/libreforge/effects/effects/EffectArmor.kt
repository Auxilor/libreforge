package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.GenericAttributeMultiplierEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player

class EffectArmor : GenericAttributeMultiplierEffect(
    "armor",
    Attribute.GENERIC_ARMOR,
    AttributeModifier.Operation.ADD_NUMBER
) {
    override fun getValue(config: Config, player: Player) =
        config.getIntFromExpression("points", player).toDouble()

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("points")) violations.add(
            ConfigViolation(
                "points",
                "You must specify the amount of points to add/remove!"
            )
        )

        if (!config.has("stack")) violations.add(
            ConfigViolation(
                "stack",
                "You must specify whether to stack effect when applied on different item!"
            )
        )

        return violations
    }
}
