package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.GenericAttributeMultiplierEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player

class EffectArmorToughness : GenericAttributeMultiplierEffect(
    "armor_toughness",
    Attribute.GENERIC_ARMOR_TOUGHNESS,
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

        return violations
    }
}
