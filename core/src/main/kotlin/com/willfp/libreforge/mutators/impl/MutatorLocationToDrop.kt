package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.EditableDropEvent

object MutatorLocationToDrop : Mutator<NoCompileData>("location_to_drop") {
    override val parameterTransformers = parameterTransformers {
        TriggerParameter.EVENT becomes TriggerParameter.LOCATION
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val event = data.event as? EditableDropEvent ?: return data

        return data.copy(
            location = event.dropLocation
        )
    }
}
