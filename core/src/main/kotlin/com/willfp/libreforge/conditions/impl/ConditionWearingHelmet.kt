package com.willfp.libreforge.conditions.impl

import com.willfp.eco.util.toSingletonList
import com.willfp.libreforge.conditions.templates.ItemCondition
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

object ConditionWearingHelmet : ItemCondition("wearing_helmet") {
    override fun getItems(entity: LivingEntity): Collection<ItemStack?> {
        return entity.equipment?.helmet.toSingletonList()
    }
}
