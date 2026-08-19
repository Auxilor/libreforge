package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Location

object MutatorValueToDistance : Mutator<NoCompileData>("value_to_distance") {
    override val description = "Sets the value to the distance between two points in the trigger."

    override val categories = setOf("value", "location")

    override val additionalInfo = listOf("No-ops if either point is missing or if they are in different worlds.")

    private val points = listOf("player", "victim", "location", "block", "projectile", "dispatcher")

    override val arguments = arguments {
        require("from", "You must specify the point to measure from!", Config::getString) {
            it in points
        }
        describe(
            "from",
            description = "The point in the trigger data to measure from.",
            type = ArgType.STRING,
            choices = points
        )
        require("to", "You must specify the point to measure to!", Config::getString) {
            it in points
        }
        describe(
            "to",
            description = "The point in the trigger data to measure to.",
            type = ArgType.STRING,
            choices = points
        )
    }

    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.VALUE)
    }

    private fun locationOf(point: String, data: TriggerData): Location? = when (point.lowercase()) {
        "player" -> data.player?.location
        "victim" -> data.victim?.location
        "location" -> data.location
        "block" -> data.block?.location
        "projectile" -> data.projectile?.location
        else -> data.dispatcher.location
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val from = locationOf(config.getString("from"), data) ?: return data
        val to = locationOf(config.getString("to"), data) ?: return data

        if (from.world == null || from.world != to.world) {
            return data
        }

        return data.copy(
            value = from.distance(to)
        )
    }
}
