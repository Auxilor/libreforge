package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.items.Items
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
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
