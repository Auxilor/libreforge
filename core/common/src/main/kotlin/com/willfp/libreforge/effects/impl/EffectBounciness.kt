package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectBounciness : AttributeEffect(
    "bounciness",
    "bounciness",
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val description = "Adds to how much the entity bounces when it lands."
    override val categories = setOf("movement", "attribute")

    override val additionalInfo = listOf("Requires Minecraft 26.2 or newer.")

    override val arguments = arguments {
        require(
            "bounciness",
            "You must specify the amount of bounciness to add!",
            description = "The amount of bounciness to add, as a fraction between 0 and 1. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% * 0.1"
        )
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("bounciness", entity as? Player)
}
