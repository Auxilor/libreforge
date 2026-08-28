package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectSpawnReinforcementsChance : AttributeEffect(
    "spawn_reinforcements_chance",
    Attribute.SPAWN_REINFORCEMENTS,
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val description = "Adds to a zombie's chance to spawn reinforcements when damaged."
    override val categories = setOf("entity", "attribute")

    override val arguments = arguments {
        require(
            "chance",
            "You must specify the amount of chance to add!",
            description = "The chance to add, as a fraction between 0 and 1. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% * 0.05"
        )
    }

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("chance", entity as? Player)
}
