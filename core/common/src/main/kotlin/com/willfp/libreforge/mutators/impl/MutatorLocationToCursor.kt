package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.FluidCollisionMode

object MutatorLocationToCursor : Mutator<NoCompileData>("location_to_cursor") {
    override val arguments = arguments {
        require("target", "You must specify a target (block or entity)!", Config::getString) {
            it in listOf("block", "entity")
        }
        require("start", "You must specify a start point! (player or victim)", Config::getString) {
            it in listOf("player", "victim")
        }
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
