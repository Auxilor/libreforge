package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectOxygenBonus : AttributeEffect(
    "oxygen_bonus",
    Attribute.OXYGEN_BONUS,
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val arguments = arguments {
        require("amount", "You must specify the oxygen bonus to add!")
    }

    override fun getValue(config: Config, entity: LivingEntity): Double =
        config.getDoubleFromExpression("amount", entity as? Player)
}
