package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectBlockReach : AttributeEffect(
    "block_reach",
    Attribute.BLOCK_INTERACTION_RANGE,
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val description = "Increases the player's block interaction range while the holder is active."
    override val categories = setOf("player", "attribute")

    override val arguments = arguments {
        require(
            "reach",
            "You must specify the amount of reach to add!",
            description = "The number of blocks to add to the interaction range. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% * 0.5"
        )
    }

    override fun canApplyTo(entity: LivingEntity): Boolean {
        return entity is Player
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("reach", entity as? Player)
}
