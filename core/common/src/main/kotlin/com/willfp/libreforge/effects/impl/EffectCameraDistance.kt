package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectCameraDistance : AttributeEffect(
    "camera_distance",
    "camera_distance",
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val description = "Adds to the distance the third person camera sits behind the player."
    override val categories = setOf("visual", "player", "attribute")

    override val arguments = arguments {
        require(
            "distance",
            "You must specify the amount of distance to add!",
            description = "The number of blocks to add to the camera distance. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% * 0.5"
        )
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("distance", entity as? Player)
}
