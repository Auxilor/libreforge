package com.willfp.libreforge.proxy.modern.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectSneakingSpeedMultiplier : AttributeEffect(
    "sneaking_speed_multiplier",
    Attribute.PLAYER_SNEAKING_SPEED,
    AttributeModifier.Operation.MULTIPLY_SCALAR_1
) {
    override val arguments = arguments {
        require("multiplier", "You must specify the speed multiplier!")
    }

    override fun canApplyTo(entity: LivingEntity): Boolean {
        return entity is Player
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("multiplier", entity as? Player) - 1
}
