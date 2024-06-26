package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectSafeFallDistance : AttributeEffect(
    "safe_fall_distance",
    Attribute.GENERIC_SAFE_FALL_DISTANCE,
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val arguments = arguments {
        require("distance", "You must specify the increase in safe fall distance!")
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("amount", entity as? Player)
}
