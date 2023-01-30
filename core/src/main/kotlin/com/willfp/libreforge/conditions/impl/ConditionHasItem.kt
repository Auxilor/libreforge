package com.willfp.libreforge.conditions.impl

import com.willfp.libreforge.conditions.templates.ItemCondition
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object ConditionHasItem : ItemCondition("has_item") {
    override fun getItems(player: Player): Collection<ItemStack?> {
        return player.inventory.contents.toList()
    }
}
