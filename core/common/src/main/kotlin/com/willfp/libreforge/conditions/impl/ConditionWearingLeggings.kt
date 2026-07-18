package com.willfp.libreforge.conditions.impl

import com.willfp.eco.util.toSingletonList
import com.willfp.libreforge.conditions.templates.ItemCondition
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

object ConditionWearingLeggings : ItemCondition("wearing_leggings") {
    override val description = "Passes when the entity is wearing the specified leggings."
    override val categories = setOf("inventory")

    override fun getItems(entity: LivingEntity): Collection<ItemStack?> {
        return entity.equipment?.leggings.toSingletonList()
    }
}
