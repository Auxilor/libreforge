package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.slot.SlotTypes
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.inventory.ItemStack

object EffectGiveItem : Effect<List<ItemStack>>("give_item") {
    override val description = "Gives the player one or more items, optionally placing them into a specific inventory slot."
    override val categories = setOf("inventory")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            listOf("item", "items"),
            "You must specify the item to give!",
            description = "The item or list of items to give to the player.",
            type = ArgType.ITEM_LIST
        )
        optional(
            "slot",
            description = "The inventory slot type to place the item into. If omitted the item is dropped into the player's inventory via telekinesis.",
            type = ArgType.STRING
        )
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
        return config.getStrings("items", "item").map {
            Items.lookup(it)
        }.filter { it !is EmptyTestableItem }.map { it.item }
    }
}
