package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.CMI
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorCmiLocationToHome : Mutator<NoCompileData>("cmi_location_to_home") {
    override val description = "Sets the location to the location of one of the player's CMI homes."
    override val categories = setOf("location", "player")
    override val additionalInfo = listOf(
        "Requires the CMI plugin.",
        "No-ops if there is no player, or if the player has no home with the given name."
    )

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.PLAYER becomes TriggerParameter.LOCATION
    }

    override val arguments = arguments {
        require(
            "home",
            "You must specify the home name!",
            description = "The name of the player's CMI home to read the location from.",
            type = ArgType.STRING
        )
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val player = data.player ?: return data
        val user = CMI.getInstance()?.playerManager?.getUser(player) ?: return data
        val location = user.getHome(config.getString("home"))?.loc?.bukkitLoc ?: return data

        return data.copy(
            location = location
        )
    }
}
