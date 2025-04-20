package com.willfp.libreforge.integrations.worldguard.impl

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class RegionEvent(
    who: Player,
    val location: Location,
    val region: String
) : PlayerEvent(who) {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }
}
