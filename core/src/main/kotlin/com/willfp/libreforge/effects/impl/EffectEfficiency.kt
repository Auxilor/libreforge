package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectEfficiency : AttributeEffect(
    "efficiency",
    Attribute.PLAYER_MINING_EFFICIENCY,
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val arguments = arguments {
        require("amount", "You must specify the amount of efficiency to add!")
    }

    override fun canApplyTo(entity: LivingEntity): Boolean {
        return entity is Player
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("amount", entity as? Player)
}
