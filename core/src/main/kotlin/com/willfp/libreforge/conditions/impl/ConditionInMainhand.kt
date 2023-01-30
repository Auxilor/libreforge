package com.willfp.libreforge.conditions.impl

import com.willfp.eco.util.toSingletonList
import com.willfp.libreforge.conditions.templates.ItemCondition
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object ConditionInMainhand : ItemCondition("in_mainhand") {
    override fun getItems(player: Player): Collection<ItemStack?> {
        return player.inventory.itemInMainHand.toSingletonList()
    }
}
