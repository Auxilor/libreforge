package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.GenericAttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player

class EffectBonusHealth : GenericAttributeEffect(
    "bonus_health",
    Attribute.GENERIC_MAX_HEALTH,
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val arguments = arguments {
        require("health", "You must specify the bonus health to give!")
    }

    override fun getValue(config: Config, player: Player) =
        config.getIntFromExpression("health", player).toDouble()
}
