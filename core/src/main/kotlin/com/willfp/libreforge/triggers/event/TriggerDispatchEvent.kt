package com.willfp.libreforge.triggers.event

import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.get
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class TriggerDispatchEvent(
    val dispatcher: Dispatcher<*>,
    val trigger: DispatchedTrigger
) : Event(), Cancellable {
    private var _cancelled = false

    @Deprecated(
        "Use dispatcher instead",
        ReplaceWith("dispatcher.get()"),
        DeprecationLevel.ERROR
    )
    val player: Player?
        get() = dispatcher.get()

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
