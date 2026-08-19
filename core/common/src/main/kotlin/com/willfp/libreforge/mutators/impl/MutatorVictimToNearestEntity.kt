package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity

object MutatorVictimToNearestEntity : Mutator<NoCompileData>("victim_to_nearest_entity") {
    override val description = "Sets the victim to the closest living entity to the location."

    override val categories = setOf("victim", "entity")

    override val additionalInfo = listOf("No-ops if there are no living entities in range.")

    override val arguments = arguments {
        require(
            "radius",
            "You must specify the radius to search in!",
            description = "The radius around the location to search for entities in.",
            type = ArgType.EXPRESSION,
            example = "8"
        )
        optional(
            "include_player",
            description = "If the trigger's player can be selected as the new victim.",
            type = ArgType.BOOLEAN,
            default = "false",
            example = "false"
        )
    }

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.LOCATION becomes TriggerParameter.VICTIM
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val location = data.location ?: return data
        val world = location.world ?: return data

        val radius = config.getDoubleFromExpression("radius", data)
        val includePlayer = config.has("include_player") && config.getBool("include_player")

        val nearest = world.getNearbyEntities(location, radius, radius, radius)
            .filterIsInstance<LivingEntity>()
            .filter { includePlayer || it != data.player }
            .minByOrNull { it.location.distanceSquared(location) }
            ?: return data

        return data.copy(victim = nearest)
    }
}
