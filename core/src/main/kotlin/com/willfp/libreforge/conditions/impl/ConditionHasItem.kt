package com.willfp.libreforge.conditions.impl

import com.willfp.libreforge.conditions.templates.ItemCondition
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object ConditionHasItem : ItemCondition("has_item") {
    override fun getItems(entity: LivingEntity): Collection<ItemStack?> {
        if (entity is Player) {
            return entity.inventory.contents.toList()
        }

        return listOf(
            entity.equipment?.helmet,
            entity.equipment?.chestplate,
            entity.equipment?.leggings,
            entity.equipment?.boots,
            entity.equipment?.itemInMainHand,
            entity.equipment?.itemInOffHand
        )
    }
}
