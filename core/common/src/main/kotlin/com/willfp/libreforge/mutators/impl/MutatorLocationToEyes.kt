package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorLocationToEyes : Mutator<NoCompileData>("location_to_eyes") {
    override val description = "Sets the location to the eye position of the player or the victim."

    override val categories = setOf("location", "player", "victim")

    override val arguments = arguments {
        require("entity", "You must specify an entity! (player or victim)", Config::getString) {
            it in listOf("player", "victim")
        }
        describe(
            "entity",
            description = "The entity to take the eye location from.",
            type = ArgType.STRING,
            choices = listOf("player", "victim")
        )
    }

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.PLAYER becomes TriggerParameter.LOCATION
        TriggerParameter.VICTIM becomes TriggerParameter.LOCATION
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val entity = if (config.getString("entity").equals("player", true)) {
            data.player
        } else {
            data.victim
        }

        return data.copy(
            location = entity?.eyeLocation
        )
    }
}
