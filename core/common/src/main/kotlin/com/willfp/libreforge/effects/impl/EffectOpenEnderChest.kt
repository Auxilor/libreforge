package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.inventory.ItemStack

object EffectOpenEnderChest : Effect<ItemStack>("open_ender_chest") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: ItemStack): Boolean {
        val player = data.player ?: return false

        player.openInventory(player.enderChest)

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): ItemStack {
        return Items.lookup(config.getString("item")).item
    }
}
