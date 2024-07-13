package com.willfp.libreforge.slot

import com.willfp.libreforge.Holder
import com.willfp.libreforge.ItemProvidedHolder
import com.willfp.libreforge.TypedProvidedHolder
import org.bukkit.inventory.ItemStack

class SlotItemProvidedHolder<T: Holder>(
    override val holder: T,
    provider: ItemStack,
    val slotType: SlotType
): ItemProvidedHolder(
    holder,
    provider
), TypedProvidedHolder<T>
