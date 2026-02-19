package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.slot.SlotType
import com.willfp.libreforge.slot.SlotTypes
import com.willfp.libreforge.slot.impl.SlotTypeAny
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorPlayerToItem : Mutator<SlotType>("player_to_item") {
    override val arguments = arguments {
        require("slot", "You must specify a slot!")
    }

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.PLAYER becomes TriggerParameter.ITEM
    }

    override fun mutate(data: TriggerData, config: Config, compileData: SlotType): TriggerData {
        data.player ?: return data
        return data.copy(
            item = compileData.getItems(data.player)[0]
        )
    }

    override fun makeCompileData(config: Config, context: ViolationContext): SlotType {
        return SlotTypes.get(config.getFormattedString("slot")) ?: return SlotTypeAny
    }
}
