package com.willfp.libreforge.dates

import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.libreforge.plugin
import java.time.LocalDateTime
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Reads the clock (in [Dates.zone]) every second and notifies listeners as hours, days and
 * months start and end. See [DateBoundaryTracker] for exactly when each fires.
 */
object DateClock {
    private class Listener(
        val period: DatePeriod,
        val edge: DateEdge,
        val action: (LocalDateTime) -> Unit
    )

    private val listeners = CopyOnWriteArrayList<Listener>()

    private val tracker = DateBoundaryTracker()

    private var task: EcoTask? = null

    /**
     * Run [listener] with the current date and time each time a [period] reaches [edge].
     */
    @JvmStatic
    fun on(period: DatePeriod, edge: DateEdge, listener: (LocalDateTime) -> Unit) {
        listeners += Listener(period, edge, listener)
    }

    /**
     * Start the clock, or restart it after the scheduler has cancelled its tasks.
     */
    @JvmStatic
    fun start() {
        task?.cancel()
        task = plugin.scheduler.global().runTimer(20, 20) {
            for (boundary in tracker.advance(LocalDateTime.now(Dates.zone))) {
                for (listener in listeners) {
                    if (listener.period == boundary.period && listener.edge == boundary.edge) {
                        listener.action(boundary.dateTime)
                    }
                }
            }
        }
    }
}
