package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectIncreaseStepHeight : AttributeEffect(
    "increase_step_height",
    Attribute.GENERIC_STEP_HEIGHT,
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val arguments = arguments {
        require("height", "You must specify the increase in step height!")
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("height", entity as? Player)
}
