package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.GenericAttributeMultiplierEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player

class EffectMovementSpeedMultiplier : GenericAttributeMultiplierEffect(
    "movement_speed_multiplier",
    Attribute.GENERIC_MOVEMENT_SPEED,
    AttributeModifier.Operation.MULTIPLY_SCALAR_1
) {
    override fun getValue(config: Config, player: Player) =
        config.getDoubleFromExpression("multiplier", player) - 1

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("multiplier")) violations.add(
            ConfigViolation(
                "multiplier",
                "You must specify the movement speed multiplier!"
            )
        )

        return violations
    }
}
