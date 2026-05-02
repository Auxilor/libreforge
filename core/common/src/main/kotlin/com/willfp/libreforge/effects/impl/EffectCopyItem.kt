package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.slot.SlotTypes
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectCopyItem : Effect<NoCompileData>("copy_item") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("from_slot", "You must specify the source slot!")
        require("to_slot", "You must specify the destination slot!")
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
