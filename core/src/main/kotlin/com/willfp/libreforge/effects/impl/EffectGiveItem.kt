package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.items.Items
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.slot.SlotTypes
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.inventory.ItemStack

object EffectGiveItem : Effect<List<ItemStack>>("give_item") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(listOf("item", "items"), "You must specify the item to give!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: List<ItemStack>): Boolean {
        val player = data.player ?: return false

        val slotType = SlotTypes[config.getString("slot")]

        if (compileData.isEmpty()) return false

        slotType?.addToSlot(player, compileData.first()) ?: run {
            DropQueue(player)
                .addItems(compileData)
                .forceTelekinesis()
                .push()
        }

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): List<ItemStack> {
        val list = config.getStrings("items").map { Items.lookup(it).item }.toMutableList()
        config.getStringOrNull("item")?.let {
            list.add(Items.lookup(it).item)
        }
        return list
    }
}
