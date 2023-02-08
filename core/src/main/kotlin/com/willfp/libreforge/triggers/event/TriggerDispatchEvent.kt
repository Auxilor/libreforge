package com.willfp.libreforge.triggers.event

import com.willfp.libreforge.triggers.DispatchedTrigger
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class TriggerDispatchEvent(
    who: Player,
    val trigger: DispatchedTrigger
) : PlayerEvent(who), Cancellable {
    private var _cancelled = false

    override fun isCancelled() = _cancelled

    override fun setCancelled(cancelled: Boolean) {
        _cancelled = cancelled
    }

    override fun getHandlers() = handlerList

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }
}
