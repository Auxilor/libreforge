package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.items.Items
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.EditableDropEvent
import org.bukkit.inventory.ItemStack

object EffectDropItem : Effect<ItemStack>("drop_item") {
    override val description = "Drops an item at the trigger location."
    override val categories = setOf("inventory")

    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require(
            "item",
            "You must specify the item to drop!",
            description = "The item to drop. Accepts eco item lookup strings.",
            type = ArgType.ITEM
        )
        optional(
            "add_to_drops",
            description = "If true and the trigger has a drop event, adds the item to that event's drop list instead of spawning it directly.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: ItemStack): Boolean {
        val location = data.location ?: return false

        // When add_to_drops is set and the trigger supplies a drop event, add the
        // item into the event's drop list so it goes through the same pipeline as
        // natural drops (multiply_drops, telekinesis). A clone is added because
        // modifiers like multiply_drops mutate the stack in place, and compileData
        // is shared across every invocation of this effect.
        if (config.getBool("add_to_drops")) {
            val dropEvent = data.event as? EditableDropEvent
            if (dropEvent != null) {
                dropEvent.drops.addStacking(compileData.clone())
                return true
            }
        }

        val player = data.player

        // Clone here too: compileData is shared across every invocation, and the
        // stack handed to a DropQueue is exposed to (and mutated by) drop queue
        // listeners.
        val item = compileData.clone()

        if (player == null) {
            location.world?.dropItem(location, item)
        } else {
            DropQueue(player)
                .setLocation(location)
                .addItem(item)
                .push()
        }

        return true
    }

    /**
     * Add [item] to the drop list, merging it into stacks that are already
     * there rather than appending a second entry for the same item, so that it
     * drops as one stack instead of one per trigger.
     */
    private fun MutableList<ItemStack>.addStacking(item: ItemStack) {
        var remaining = item.amount

        for (existing in this) {
            if (remaining <= 0) {
                break
            }

            if (!existing.isSimilar(item)) {
                continue
            }

            val space = existing.maxStackSize - existing.amount

            if (space <= 0) {
                continue
            }

            val toAdd = minOf(space, remaining)
            existing.amount += toAdd
            remaining -= toAdd
        }

        if (remaining > 0) {
            add(item.apply { amount = remaining })
        }
    }

    override fun makeCompileData(config: Config, context: ViolationContext): ItemStack {
        return Items.lookup(config.getString("item")).item
    }
}
