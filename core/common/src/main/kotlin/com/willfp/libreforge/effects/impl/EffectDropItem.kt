package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.items.Items
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.EditableDropEvent
import org.bukkit.inventory.ItemStack

object EffectDropItem : Effect<ItemStack>("drop_item") {
    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require("item", "You must specify the item to drop!")
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
                dropEvent.drops.add(compileData.clone())
                return true
            }
        }

        val player = data.player

        if (player == null) {
            location.world?.dropItem(location, compileData)
        } else {
            DropQueue(player)
                .setLocation(location)
                .addItem(compileData)
                .push()
        }

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): ItemStack {
        return Items.lookup(config.getString("item")).item
    }
}
