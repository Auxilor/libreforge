package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.GenericAttributeMultiplierEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player

class EffectBonusHealth : GenericAttributeMultiplierEffect(
    "bonus_health",
    Attribute.GENERIC_MAX_HEALTH,
    AttributeModifier.Operation.ADD_NUMBER
) {
    override fun getValue(config: Config, player: Player) =
        config.getIntFromExpression("health", player).toDouble()

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("health")) violations.add(
            ConfigViolation(
                "health",
                "You must specify the bonus health to give!"
            )
        )

        return violations
    }
}
