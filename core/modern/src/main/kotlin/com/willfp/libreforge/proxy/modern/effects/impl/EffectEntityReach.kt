package com.willfp.libreforge.proxy.modern.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectEntityReach : AttributeEffect(
    "entity_reach",
    Attribute.PLAYER_ENTITY_INTERACTION_RANGE,
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val arguments = arguments {
        require("reach", "You must specify the amount of reach to add!")
    }

    override fun canApplyTo(entity: LivingEntity): Boolean {
        return entity is Player
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("reach", entity as? Player)
}
