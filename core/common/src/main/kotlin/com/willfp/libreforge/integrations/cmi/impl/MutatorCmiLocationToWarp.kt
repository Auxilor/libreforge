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

object MutatorCmiLocationToWarp : Mutator<NoCompileData>("cmi_location_to_warp") {
    override val description = "Sets the location to the location of a CMI warp."
    override val categories = setOf("location")
    override val additionalInfo = listOf(
        "Requires the CMI plugin.",
        "No-ops if no warp with the given name exists."
    )

    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.LOCATION)
    }

    override val arguments = arguments {
        require(
            "warp",
            "You must specify the warp name!",
            description = "The name of the CMI warp to read the location from.",
            type = ArgType.STRING
        )
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val location = CMI.getInstance()?.warpManager
            ?.getWarp(config.getString("warp"))
            ?.loc
            ?.bukkitLoc
            ?: return data

        return data.copy(
            location = location
        )
    }
}
