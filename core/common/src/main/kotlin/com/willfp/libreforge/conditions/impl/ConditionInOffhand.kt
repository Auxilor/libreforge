package com.willfp.libreforge.conditions.impl

import com.willfp.eco.util.toSingletonList
import com.willfp.libreforge.conditions.templates.ItemCondition
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

object ConditionInOffhand : ItemCondition("in_offhand") {
    override val description = "Passes when the entity is holding a matching item in their off hand."

    override val categories = setOf("inventory")

    override fun getItems(entity: LivingEntity): Collection<ItemStack?> {
        return entity.equipment?.itemInOffHand.toSingletonList()
    }
}
