package com.willfp.libreforge.internal.api

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class AsyncPlayerMoveEvent(
    val from: Location,
    val to: Location,
    val player: Player
) : Event(true), Cancellable {

    private var cancelled = false

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        this.cancelled = cancel
    }

    fun hasExplicitlyChangedBlock(): Boolean {
        return from.blockX != to.blockX || from.blockY != to.blockY || from.blockZ != to.blockZ || from.world.uid != to.world.uid
    }

    companion object {
        private val HANDLER_LIST = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLER_LIST
        }
    }

}