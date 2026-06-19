package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectEntityReach : AttributeEffect(
    "entity_reach",
    Attribute.ENTITY_INTERACTION_RANGE,
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val description = "Increases the player's entity interaction range while the holder is active."
    override val categories = setOf("player", "attribute")

    override val arguments = arguments {
        require(
            "reach",
            "You must specify the amount of reach to add!",
            description = "The number of blocks to add to the entity interaction range. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun canApplyTo(entity: LivingEntity): Boolean {
        return entity is Player
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("reach", entity as? Player)
}
