package com.willfp.libreforge.integrations.plotsquared

import com.plotsquared.core.events.PlotEvent
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * PlotSquared events are not Bukkit events, so they are wrapped in one to be carried
 * through [com.willfp.libreforge.triggers.TriggerData], which lets filters read them.
 *
 * This event is never called through the Bukkit event system, it only exists as a carrier.
 */
class PlotSquaredEventWrapper(
    val handle: PlotEvent
) : Event() {
    override fun getHandlers(): HandlerList = HANDLERS

    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLERS
    }
}

/**
 * Get the PlotSquared event carried by the trigger's event, if there is one.
 */
internal inline fun <reified T : PlotEvent> Event?.asPlotSquaredEvent(): T? =
    ((this as? PlotSquaredEventWrapper)?.handle) as? T
