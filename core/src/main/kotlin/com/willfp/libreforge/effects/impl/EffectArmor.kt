package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.GenericAttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player

object EffectArmor : GenericAttributeEffect(
    "armor",
    Attribute.GENERIC_ARMOR,
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val arguments = arguments {
        require("points", "You must specify the amount of points to add/remove!")
    }

    override fun getValue(config: Config, player: Player) =
        config.getIntFromExpression("points", player).toDouble()
}
