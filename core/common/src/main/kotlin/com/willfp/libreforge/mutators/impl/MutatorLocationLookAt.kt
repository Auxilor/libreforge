package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.triggers.TriggerData

object MutatorLocationLookAt : Mutator<NoCompileData>("location_look_at") {
    override val description = "Rotates the location to face towards another point in the trigger."

    override val categories = setOf("location")

    override val additionalInfo = listOf(
        "Only changes the direction of the location, not its position.",
        "No-ops if the target is missing or is at the exact same position as the location."
    )

    override val arguments = arguments {
        require("target", "You must specify a target to look at!", Config::getString) {
            it in listOf("player", "victim", "block", "projectile", "dispatcher")
        }
        describe(
            "target",
            description = "The point in the trigger data to face towards.",
            type = ArgType.STRING,
            choices = listOf("player", "victim", "block", "projectile", "dispatcher")
        )
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val location = data.location?.clone() ?: return data

        val target = when (config.getString("target").lowercase()) {
            "player" -> data.player?.location
            "victim" -> data.victim?.location
            "block" -> data.block?.location?.add(0.5, 0.5, 0.5)
            "projectile" -> data.projectile?.location
            else -> data.dispatcher.location
        } ?: return data

        val direction = target.toVector().subtract(location.toVector())

        if (direction.lengthSquared() == 0.0) {
            return data
        }

        location.direction = direction

        return data.copy(location = location)
    }
}
