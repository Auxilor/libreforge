package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectBelowNameDistance : AttributeEffect(
    "below_name_distance",
    "below_name_distance",
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val description = "Adds to the distance at which the entity's below name text is visible."
    override val categories = setOf("visual", "entity", "attribute")

    override val additionalInfo = listOf("Requires Minecraft 26.2 or newer.")

    override val arguments = arguments {
        require(
            "distance",
            "You must specify the amount of distance to add!",
            description = "The number of blocks to add to the below name distance. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% * 2"
        )
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("distance", entity as? Player)
}
