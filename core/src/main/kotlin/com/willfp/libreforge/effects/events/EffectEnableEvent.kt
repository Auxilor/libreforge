package com.willfp.libreforge.effects.events

import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.get
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class EffectEnableEvent(
    val dispatcher: Dispatcher<*>,
    val effect: Effect<*>,
    val holder: ProvidedHolder
) : Event() {
    override fun getHandlers() = handlerList

    @Deprecated(
        "Use dispatcher.get()",
        ReplaceWith("dispatcher.get()"),
        DeprecationLevel.ERROR
    )
    val player: Player?
        get() = dispatcher.get()

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }
}
