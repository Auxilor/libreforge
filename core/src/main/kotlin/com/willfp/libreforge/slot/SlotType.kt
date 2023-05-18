package com.willfp.libreforge.slot

import com.willfp.eco.core.registry.KRegistrable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class SlotType(
    override val id: String
) : KRegistrable {
    abstract fun addToSlot(player: Player, item: ItemStack): Boolean
}
