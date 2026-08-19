package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.slot.SlotTypes
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorItemToSlot : Mutator<NoCompileData>("item_to_slot") {
    override val description = "Sets the item to the item held in a given slot."

    override val categories = setOf("inventory", "player", "victim")

    override val additionalInfo = listOf("No-ops if the slot is invalid or if the entity has no item in that slot.")

    override val arguments = arguments {
        require(
            "slot",
            "You must specify the slot to take the item from!",
            description = "The slot to take the item from, e.g. mainhand, offhand, helmet, or an inventory slot number.",
            type = ArgType.STRING,
            example = "helmet"
        )
        optional(
            "entity",
            description = "The entity to read the slot from.",
            type = ArgType.STRING,
            default = "player",
            choices = listOf("player", "victim"),
            example = "player"
        )
    }

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.PLAYER becomes TriggerParameter.ITEM
        TriggerParameter.VICTIM becomes TriggerParameter.ITEM
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val entity = if (config.has("entity") && config.getString("entity").equals("victim", true)) {
            data.victim
        } else {
            data.player
        } ?: return data

        val slot = SlotTypes[config.getString("slot").lowercase()] ?: return data

        return data.copy(
            item = slot.getItems(entity).firstOrNull() ?: return data
        )
    }
}
