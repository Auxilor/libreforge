package com.willfp.libreforge.conditions.impl

import com.willfp.eco.util.toSingletonList
import com.willfp.libreforge.conditions.templates.ItemCondition
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

object ConditionWearingHelmet : ItemCondition("wearing_helmet") {
    override val description = "Passes when the entity is wearing the specified helmet."
    override val categories = setOf("inventory")

    override fun getItems(entity: LivingEntity): Collection<ItemStack?> {
        return entity.equipment?.helmet.toSingletonList()
    }
}
