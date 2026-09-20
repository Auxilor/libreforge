package com.willfp.libreforge.integrations.luckperms

import net.luckperms.api.event.LuckPermsEvent
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * LuckPerms events are not Bukkit events, so they are wrapped in one to be carried
 * through [com.willfp.libreforge.triggers.TriggerData], which lets filters read them.
 *
 * This event is never called through the Bukkit event system, it only exists as a carrier.
 */
class LuckPermsEventWrapper(
    val handle: LuckPermsEvent
) : Event() {
    override fun getHandlers(): HandlerList = HANDLERS

    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLERS
    }
}

/**
 * Get the LuckPerms event carried by the trigger's event, if there is one.
 */
internal inline fun <reified T : LuckPermsEvent> Event?.asLuckPermsEvent(): T? =
    ((this as? LuckPermsEventWrapper)?.handle) as? T
