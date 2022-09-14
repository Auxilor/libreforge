package com.willfp.libreforge.events

import com.willfp.libreforge.Holder
import com.willfp.libreforge.effects.NamedArgument
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class TriggerCreatePlaceholdersEvent(
    who: Player,
    val holder: Holder,
    val trigger: Trigger,
    val data: TriggerData,
    val value: Double
) : PlayerEvent(who) {
    private val _placeholders = mutableMapOf<String, NamedArgument>()

    val placeholders: Set<NamedArgument>
        get() = _placeholders.values.toSet()

    /**
     * Add a placeholder to the invocation.
     */
    fun addPlaceholder(placeholder: NamedArgument) {
        for (identifier in placeholder.identifiers) {
            _placeholders[identifier] = placeholder
        }
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