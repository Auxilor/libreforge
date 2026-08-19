package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorItemToVictimHand : Mutator<NoCompileData>("item_to_victim_hand") {
    override val description = "Sets the item to whatever the victim is holding in their main hand."

    override val categories = setOf("inventory", "victim")

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.VICTIM becomes TriggerParameter.ITEM
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            item = data.victim?.equipment?.itemInMainHand
        )
    }
}
