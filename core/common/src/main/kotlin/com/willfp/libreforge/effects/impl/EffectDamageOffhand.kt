package com.willfp.libreforge.effects.impl

import com.willfp.eco.util.toSingletonList
import com.willfp.libreforge.effects.templates.DamageItemEffect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.inventory.ItemStack


object EffectDamageOffhand : DamageItemEffect("damage_offhand") {
    override val description = "Applies durability damage to the item in the victim's off hand."
    override val categories = setOf("combat", "inventory")

    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override fun getItems(data: TriggerData): List<ItemStack> {
        return data.victim?.equipment?.itemInOffHand.toSingletonList()
    }
}
