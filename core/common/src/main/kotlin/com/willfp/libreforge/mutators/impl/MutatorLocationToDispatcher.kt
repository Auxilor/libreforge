package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorLocationToDispatcher : Mutator<NoCompileData>("location_to_dispatcher") {
    override val description = "Sets the location to the dispatcher's current position."

    override val categories = setOf("location", "meta")

    override val additionalInfo = listOf("No-ops if the dispatcher has no location, e.g. the global dispatcher.")

    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.LOCATION)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            location = data.dispatcher.location ?: return data
        )
    }
}
