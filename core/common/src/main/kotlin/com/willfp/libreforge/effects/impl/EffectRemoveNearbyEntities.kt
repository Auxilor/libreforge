package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getFormattedStrings
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player

object EffectRemoveNearbyEntities : Effect<NoCompileData>("remove_nearby_entities") {
    override val description = "Removes all nearby entities within a radius. Players are never removed."
    override val categories = setOf("entity", "world")

    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require(
            "radius",
            "You must specify the radius!",
            description = "The radius to remove entities within. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "5 + %level% * 0.5"
        )
        optional(
            "entities",
            description = "If specified, only these entity types will be removed. Supports placeholders.",
            type = ArgType.ENTITY_LIST
        )
        optional(
            "remove_named",
            description = "Whether entities with a custom name are removed too.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val location = data.location ?: return false
        val world = location.world ?: return false

        val radius = config.getDoubleFromExpression("radius", data)
        val removeNamed = config.getBool("remove_named")

        val entities = config.getFormattedStrings("entities", data).map { Entities.lookup(it) }

        var removed = false

        for (entity in world.getNearbyEntities(location, radius, radius, radius)) {
            if (entity is Player) {
                continue
            }

            if (!removeNamed && entity.customName() != null) {
                continue
            }

            if (entities.isNotEmpty() && entities.none { it.matches(entity) }) {
                continue
            }

            entity.remove()
            removed = true
        }

        return removed
    }
}
