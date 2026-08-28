package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectAttackDamage : AttributeEffect(
    "attack_damage",
    Attribute.ATTACK_DAMAGE,
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val description = "Adds to the entity's base attack damage attribute."
    override val categories = setOf("combat", "attribute")

    override val additionalInfo = listOf(
        "Modifies the attack damage attribute itself, so it applies to every attack and shows in the item tooltip.",
        "To scale the damage of a specific attack instead, use damage_multiplier or add_damage.",
    )

    override val arguments = arguments {
        require(
            "damage",
            "You must specify the amount of damage to add!",
            description = "The amount of attack damage to add. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% * 0.5"
        )
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("damage", entity as? Player)
}
