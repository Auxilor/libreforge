package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.FluidCollisionMode

object MutatorLocationToCursor : Mutator<NoCompileData>("location_to_cursor") {
    override val description = "Re-maps the location to the position of the entity or block at the player's or victim's cursor."

    override val categories = setOf("location")

    override val additionalInfo = listOf("Sets location to null if the raytrace hits nothing.")

    override val arguments = arguments {
        require("target", "You must specify a target (block or entity)!", Config::getString) {
            it in listOf("block", "entity")
        }
        describe(
            "target",
            description = "The entity or block to target with the raytrace.",
            type = ArgType.STRING,
            choices = listOf("block", "entity")
        )
        require("start", "You must specify a start point! (player or victim)", Config::getString) {
            it in listOf("player", "victim")
        }
        describe(
            "start",
            description = "The entity to raytrace from.",
            type = ArgType.STRING,
            choices = listOf("player", "victim")
        )
    }

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.PLAYER becomes TriggerParameter.LOCATION
        TriggerParameter.VICTIM becomes TriggerParameter.LOCATION
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val target = config.getString("target")
        val startingEntity = config.getString("start")

        val start = if (startingEntity.equals("player", true)) {
            data.player
        } else {
            data.victim
        }

        val result = start?.world?.rayTrace(
            start.location,
            start.eyeLocation.direction,
            plugin.configYml.getDouble("raytrace-distance"),
            FluidCollisionMode.NEVER,
            true,
            0.0,
            null
        ) ?: return data.copy(location = null)

        val location = when {
            target.equals("block", true) -> result.hitBlock?.location
            else -> result.hitEntity?.location
        } ?: return data.copy(location = null)


        return data.copy(
            location = location
        )
    }
}