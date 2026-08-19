package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorItemToOffhand : Mutator<NoCompileData>("item_to_offhand") {
    override val description = "Sets the item to whatever the player is holding in their offhand."

    override val categories = setOf("inventory", "player")

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.PLAYER becomes TriggerParameter.ITEM
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            item = data.player?.inventory?.itemInOffHand
        )
    }
}
