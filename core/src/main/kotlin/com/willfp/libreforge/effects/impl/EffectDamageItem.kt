package com.willfp.libreforge.effects.impl

import com.willfp.eco.util.toSingletonList
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.DamageItemEffect
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.inventory.ItemStack


object EffectDamageItem : DamageItemEffect("damage_item") {
    override val isPermanent = false

    override fun getItems(data: TriggerData): List<ItemStack> {
        return data.foundItem.toSingletonList()
    }
}
