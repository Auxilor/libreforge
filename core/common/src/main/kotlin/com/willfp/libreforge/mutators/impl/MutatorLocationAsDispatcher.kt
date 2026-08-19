package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData

object MutatorLocationAsDispatcher : Mutator<NoCompileData>("location_as_dispatcher") {
    override val description = "Sets the dispatcher to the current location."

    override val categories = setOf("location", "meta")

    override val additionalInfo = listOf("No-ops if the trigger data has no location.")

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            dispatcher = data.location?.toDispatcher() ?: return data
        )
    }
}
