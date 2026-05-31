package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectArmorToughness : AttributeEffect(
    "armor_toughness",
    Attribute.ARMOR_TOUGHNESS,
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val description = "Permanently increases or decreases the player's armor toughness attribute while the holder is active."
    override val categories = setOf("combat", "player")

    override val arguments = arguments {
        require(
            "points",
            "You must specify the amount of points to add/remove!",
            description = "The number of armor toughness points to add (or subtract if negative). Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun getValue(config: Config, entity: LivingEntity): Double =
        config.getDoubleFromExpression("points", entity as? Player)
}
