package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectWaypointTransmitRange : AttributeEffect(
    "waypoint_transmit_range",
    "waypoint_transmit_range",
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val description = "Adds to the range at which the entity transmits its waypoint to others."
    override val categories = setOf("player", "attribute")

    override val arguments = arguments {
        require(
            "range",
            "You must specify the amount of range to add!",
            description = "The number of blocks to add to the waypoint transmit range. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% * 100"
        )
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("range", entity as? Player)
}
