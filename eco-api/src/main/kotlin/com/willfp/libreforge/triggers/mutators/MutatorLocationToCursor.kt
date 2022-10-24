package com.willfp.libreforge.triggers.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.triggers.DataMutator
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.FluidCollisionMode

class MutatorLocationToCursor : DataMutator(
    "location_to_cursor"
) {
    override fun mutate(data: TriggerData, config: Config): TriggerData {
        val type = config.getString("raytrace-type")

        val eType = config.getString("starting-entity")

        val limit = config.getDoubleFromExpression("limit", data.player)

        val target = if (eType.equals("player", true)) {
            data.player
        } else {
            data.victim
        }

        val result = target?.world?.rayTrace(target.location, target.eyeLocation.direction,
            limit, FluidCollisionMode.NEVER, true, 0.0, null)?: return data

        val location = when {
            type.equals("block", true) -> result.hitBlock?.location
            else -> result.hitEntity?.location
        }?: return data


        return data.copy(
            location = location
        )
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("raytrace-type")) {
            violations.add(ConfigViolation("raytrace-type", "Missing raytrace type"))
        } else {
            val type = config.getString("type")
            if (type notInIgnoreCase listOf("block", "entity")) {
                violations.add(ConfigViolation("raytrace-type", "Invalid raytrace type"))
            }
        }

        if (!config.has("starting-entity")) {
            violations.add(ConfigViolation("starting-entity", "Missing starting entity"))
        } else {
            val type = config.getString("starting-entity")
            if (type notInIgnoreCase listOf("player", "victim")) {
                violations.add(ConfigViolation("starting-entity", "Invalid starting entity"))
            }
        }

        if (!config.has("limit")) {
            violations.add(ConfigViolation("limit", "Missing limit"))
        }

        return violations
    }

    private infix fun String.notInIgnoreCase(other: List<String>): Boolean {
        return other.containsIgnoreCase(this)
    }
}
