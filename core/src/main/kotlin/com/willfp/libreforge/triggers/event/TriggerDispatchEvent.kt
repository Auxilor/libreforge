package com.willfp.libreforge.triggers.event

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class TriggerDispatchEvent(
    who: Player,
    trigger: Trigger,
    data: TriggerData
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
