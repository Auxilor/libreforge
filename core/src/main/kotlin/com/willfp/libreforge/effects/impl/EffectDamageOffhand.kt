package com.willfp.libreforge.effects.impl

import com.willfp.eco.util.toSingletonList
import com.willfp.libreforge.effects.templates.DamageItemEffect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.inventory.ItemStack


object EffectDamageOffhand : DamageItemEffect("damage_offhand") {
    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override fun getItems(data: TriggerData): List<ItemStack> {
        return data.victim?.equipment?.itemInOffHand.toSingletonList()
    }
}
