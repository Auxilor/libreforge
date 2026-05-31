package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectIncreaseStepHeight : AttributeEffect(
    "increase_step_height",
    Attribute.STEP_HEIGHT,
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val description = "Increases the player's step height, allowing them to walk up taller blocks without jumping."
    override val categories = setOf("movement", "player", "attribute")

    override val arguments = arguments {
        require(
            "height",
            "You must specify the increase in step height!",
            description = "The number of extra blocks the player can step up automatically. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("height", entity as? Player)
}
