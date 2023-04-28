package com.willfp.libreforge.effects.events

import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.effects.Effect
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class EffectDisableEvent(
    who: Player,
    val effect: Effect<*>,
    val holder: ProvidedHolder
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
