package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectAttackSpeedMultiplier : AttributeEffect(
    "attack_speed_multiplier",
    Attribute.ATTACK_SPEED,
    AttributeModifier.Operation.MULTIPLY_SCALAR_1
) {
    override val description = "Multiplies the player's attack speed while the holder is active."
    override val categories = setOf("combat", "player", "attribute")

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the attack speed multiplier!",
            description = "The attack speed multiplier. 1.5 = 50% faster. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1 + %level% * 0.05"
        )
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("multiplier", entity as? Player) - 1
}
