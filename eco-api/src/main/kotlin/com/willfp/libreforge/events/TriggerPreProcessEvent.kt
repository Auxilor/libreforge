package com.willfp.libreforge.events

import com.willfp.libreforge.triggers.Trigger
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class TriggerPreProcessEvent(
    who: Player,
    val trigger: Trigger,
    val value: Double
) : PlayerEvent(who), Cancellable {
    /**
     * If the event is cancelled.
     */
    private var cancelled = false

    /**
     * Get if the effect activation is cancelled.
     *
     * @return If cancelled.
     */
    override fun isCancelled(): Boolean {
        return cancelled
    }

    /**
     * Set if the event is cancelled.
     *
     * @param cancel If cancelled.
     */
    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

    /**
     * Bukkit parity.
     *
     * @return The handlers.
     */
    override fun getHandlers(): HandlerList {
        return HANDLERS
    }

    companion object {
        /**
         * Bukkit parity.
         */
        @JvmStatic
        private val HANDLERS: HandlerList = HandlerList()

        /**
         * Bukkit parity.
         *
         * @return The handler list.
         */
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }
}
