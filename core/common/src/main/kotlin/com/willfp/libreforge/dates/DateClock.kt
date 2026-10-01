package com.willfp.libreforge.dates

import com.willfp.libreforge.plugin
import java.time.LocalDate
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Watches for the date changing (in [Dates.zone]) and notifies listeners at each new day.
 */
object DateClock {
    private val listeners = CopyOnWriteArrayList<(LocalDate) -> Unit>()

    private var isStarted = false

    private var lastDate: LocalDate? = null

    /**
     * Run [listener] with the new date each time the day changes. Starts the clock if needed.
     */
    @JvmStatic
    fun onNewDay(listener: (LocalDate) -> Unit) {
        listeners += listener
        start()
    }

    private fun start() {
        if (isStarted) {
            return
        }

        isStarted = true

        plugin.scheduler.runTimer(20, 20) {
            val today = LocalDate.now(Dates.zone)
            val previous = lastDate
            lastDate = today

            // First tick only records the date, so a restart doesn't re-fire today's listeners
            if (previous == null || previous == today) {
                return@runTimer
            }

            for (listener in listeners) {
                listener(today)
            }
        }
    }
}
