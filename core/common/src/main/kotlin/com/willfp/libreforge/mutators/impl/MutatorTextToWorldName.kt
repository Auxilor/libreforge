package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorTextToWorldName : Mutator<NoCompileData>("text_to_world_name") {
    override val description = "Sets the text to the name of the world that the location is in."

    override val categories = setOf("chat", "world")

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.LOCATION becomes TriggerParameter.TEXT
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            text = data.location?.world?.name
        )
    }
}
