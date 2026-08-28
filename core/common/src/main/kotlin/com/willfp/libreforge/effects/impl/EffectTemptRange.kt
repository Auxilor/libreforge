package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectTemptRange : AttributeEffect(
    "tempt_range",
    Attribute.TEMPT_RANGE,
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val description = "Adds to the range at which a mob is tempted by food held by a player."
    override val categories = setOf("entity", "attribute")

    override val arguments = arguments {
        require(
            "range",
            "You must specify the amount of range to add!",
            description = "The number of blocks to add to the tempt range. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% * 2"
        )
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("range", entity as? Player)
}
