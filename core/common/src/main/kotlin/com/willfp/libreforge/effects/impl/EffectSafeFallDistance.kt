package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectSafeFallDistance : AttributeEffect(
    "safe_fall_distance",
    Attribute.SAFE_FALL_DISTANCE,
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val description = "Increases the player's safe fall distance, reducing fall damage taken."
    override val categories = setOf("movement", "player", "attribute")

    override val arguments = arguments {
        require(
            "distance",
            "You must specify the increase in safe fall distance!",
            description = "The number of extra blocks the player can fall safely. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("distance", entity as? Player)
}
