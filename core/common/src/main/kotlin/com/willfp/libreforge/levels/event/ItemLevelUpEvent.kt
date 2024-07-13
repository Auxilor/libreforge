package com.willfp.libreforge.levels.event

import com.willfp.libreforge.levels.LevelType
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import org.bukkit.inventory.ItemStack

class ItemLevelUpEvent(
    who: Player,
    val item: ItemStack,
    val level: Int,
    val type: LevelType
) : PlayerEvent(who) {
    override fun getHandlers() = handlerList

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }
}
