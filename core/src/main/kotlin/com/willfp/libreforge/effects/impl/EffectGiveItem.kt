package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object EffectGiveItem : Effect<ItemStack>("give_item") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("item", "You must specify the item to give!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: ItemStack): Boolean {
        val player = data.player ?: return false

        when (val slotType = config.getString("slot").lowercase()) {
            "hand" -> HandSlot.addToSlot(player, compileData)
            "offhand" -> SlotSlot(40).addToSlot(player, compileData)
            "helmet" -> SlotSlot(39).addToSlot(player, compileData)
            "chestplate" -> SlotSlot(38).addToSlot(player, compileData)
            "leggings" -> SlotSlot(37).addToSlot(player, compileData)
            "boots" -> SlotSlot(36).addToSlot(player, compileData)
            else -> {
                val slot = slotType.toIntOrNull()

                if (slot != null) {
                    SlotSlot(slot).addToSlot(player, compileData)
                } else {
                    AnySlot.addToSlot(player, compileData)
                }
            }
        }

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): ItemStack {
        return Items.lookup(config.getString("item")).item
    }

    private interface SlotType {
        fun addToSlot(player: Player, item: ItemStack)
    }

    private object HandSlot : SlotType {
        override fun addToSlot(player: Player, item: ItemStack) {
            if (EmptyTestableItem().matches(player.inventory.itemInMainHand)) {
                player.inventory.setItem(
                    player.inventory.heldItemSlot, item
                )
            } else {
                DropQueue(player).addItem(item).forceTelekinesis().push()
            }
        }
    }

    private class SlotSlot(val slot: Int) : SlotType {
        override fun addToSlot(player: Player, item: ItemStack) {
            if (EmptyTestableItem().matches(player.inventory.getItem(slot))) {
                player.inventory.setItem(
                    slot, item
                )
            } else {
                DropQueue(player).addItem(item).forceTelekinesis().push()
            }
        }
    }

    private object AnySlot : SlotType {
        override fun addToSlot(player: Player, item: ItemStack) {
            DropQueue(player).addItem(item).forceTelekinesis().push()
        }
    }
}
