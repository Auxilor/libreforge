package com.willfp.libreforge.effects.impl

import com.willfp.eco.util.toSingletonList
import com.willfp.libreforge.effects.templates.DamageItemEffect
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.inventory.ItemStack


object EffectDamageItem : DamageItemEffect("damage_item") {
    override val description = "Applies durability damage to the triggering item."
    override val categories = setOf("inventory")

    override val isPermanent = false

    override fun getItems(data: TriggerData): List<ItemStack> {
        return data.foundItem.toSingletonList()
    }
}
