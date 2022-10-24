package com.willfp.libreforge.triggers.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.DataMutator
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.FluidCollisionMode

object MutatorLocationToCursor : DataMutator(
    "location_to_cursor"
) {
    override fun mutate(data: TriggerData, config: Config): TriggerData {
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
            plugin.configYml.getDoubleFromExpression("raytrace-distance", data),
            FluidCollisionMode.NEVER,
            true,
            0.0,
            null
        ) ?: return data

        val location = when {
            target.equals("block", true) -> result.hitBlock?.location
            else -> result.hitEntity?.location
        } ?: return data


        return data.copy(
            location = location
        )
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (config.has("target")) {
            if (config.getString("target") !in listOf("block", "entity")) violations.add(
                ConfigViolation(
                    "target",
                    "Invalid raytrace target! Must be block or entity"
                )
            )
        } else violations.add(
            ConfigViolation(
                "target",
                "You must specify the raytrace target!"
            )
        )

        if (config.has("start")) {
            if (config.getString("start") !in listOf("player", "victim")) violations.add(
                ConfigViolation(
                    "start",
                    "Invalid start point! Must be player or victim"
                )
            )
        } else violations.add(
            ConfigViolation(
                "start",
                "You must specify the start point!"
            )
        )

        return violations
    }
}
