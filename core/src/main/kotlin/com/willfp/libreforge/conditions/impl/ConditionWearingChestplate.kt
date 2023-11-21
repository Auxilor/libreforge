package com.willfp.libreforge.conditions.impl

import com.willfp.eco.util.toSingletonList
import com.willfp.libreforge.conditions.templates.ItemCondition
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object ConditionWearingChestplate : ItemCondition("wearing_chestplate") {
    override fun getItems(entity: LivingEntity): Collection<ItemStack?> {
        return entity.equipment?.chestplate.toSingletonList()
    }
}
