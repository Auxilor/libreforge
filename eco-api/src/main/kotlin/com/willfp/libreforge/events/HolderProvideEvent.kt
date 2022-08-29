package com.willfp.libreforge.events

import com.willfp.libreforge.Holder
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class HolderProvideEvent(
    who: Player,
    val holders: MutableList<Holder>
) : PlayerEvent(who) {
    /**
     * If the event is cancelled.
     */
    private var cancelled = false

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
