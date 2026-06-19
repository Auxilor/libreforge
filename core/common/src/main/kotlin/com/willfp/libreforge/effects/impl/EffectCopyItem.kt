package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.slot.SlotTypes
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectCopyItem : Effect<NoCompileData>("copy_item") {
    override val description = "Copies the item from one inventory slot into another slot."
    override val categories = setOf("inventory")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "from_slot",
            "You must specify the source slot!",
            description = "The slot to copy the item from, e.g. mainhand or offhand.",
            type = ArgType.STRING
        )
        require(
            "to_slot",
            "You must specify the destination slot!",
            description = "The slot to copy the item into.",
            type = ArgType.STRING
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val fromSlot = SlotTypes[config.getString("from_slot")]?.getItemSlots(player)?.firstOrNull() ?: return false
        val toSlot = SlotTypes[config.getString("to_slot")]?.getItemSlots(player)?.firstOrNull() ?: return false
        val item = player.inventory.getItem(fromSlot)?.clone() ?: return false
        player.inventory.setItem(toSlot, item)
        return true
    }
}
