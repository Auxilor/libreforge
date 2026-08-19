package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorTextToVictimName : Mutator<NoCompileData>("text_to_victim_name") {
    override val description = "Sets the text to the name of the victim."

    override val categories = setOf("chat", "victim")

    override val additionalInfo = listOf("Uses the custom name of the victim if it has one.")

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.VICTIM becomes TriggerParameter.TEXT
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val victim = data.victim ?: return data.copy(text = null)

        return data.copy(
            text = victim.customName ?: victim.name
        )
    }
}
