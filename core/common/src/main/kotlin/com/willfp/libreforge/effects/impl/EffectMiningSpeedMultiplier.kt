package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectMiningSpeedMultiplier : AttributeEffect(
    "mining_speed_multiplier",
    Attribute.BLOCK_BREAK_SPEED,
    AttributeModifier.Operation.MULTIPLY_SCALAR_1
) {
    override val description = "Multiplies the player's overall block-break speed while the holder is active."
    override val categories = setOf("world", "player")

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the mining speed multiplier!",
            description = "The mining speed multiplier to apply (e.g. 2 = double speed). Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun canApplyTo(entity: LivingEntity): Boolean {
        return entity is Player
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("multiplier", entity as? Player) - 1
}
