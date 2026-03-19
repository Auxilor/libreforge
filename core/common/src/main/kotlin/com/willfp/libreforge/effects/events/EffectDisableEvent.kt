package com.willfp.libreforge.effects.events

import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.effects.Effect
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class EffectDisableEvent(
    val dispatcher: Dispatcher<*>,
    val effect: Effect<*>,
    val holder: ProvidedHolder
) : Event() {
    override fun getHandlers() = handlerList


    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }
}
