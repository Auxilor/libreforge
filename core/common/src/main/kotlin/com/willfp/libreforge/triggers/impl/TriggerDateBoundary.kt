package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.dates.DateEdge
import com.willfp.libreforge.dates.DatePeriod
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

/**
 * `hour_start`, `hour_end`, `day_start`, `day_end`, `month_start` and `month_end`,
 * fired by [com.willfp.libreforge.dates.DateClock].
 */
class TriggerDateBoundary private constructor(
    val period: DatePeriod,
    val edge: DateEdge
) : Trigger("${period.name.lowercase()}_${edge.name.lowercase()}") {
    private val periodName = period.name.lowercase()

    override val description = when (edge) {
        DateEdge.START -> "Fires for every online player when a new $periodName begins."
        DateEdge.END -> "Fires for every online player in the last minute of each $periodName."
    }

    override val categories = setOf("date")

    override val additionalInfo = listOfNotNull(
        "Times are in the timezone set by dates.timezone in config.yml.",
        when (period) {
            DatePeriod.HOUR -> "Fires at minute :59."
            DatePeriod.DAY -> "Fires at 23:59."
            DatePeriod.MONTH -> "Fires at 23:59 on the last day of the month."
        }.takeIf { edge == DateEdge.END },
        "Date and time conditions and filters still see the $periodName that is ending."
            .takeIf { edge == DateEdge.END },
        "Only players online at that moment are triggered."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    companion object {
        /**
         * One trigger for each edge of each period.
         */
        val values: List<TriggerDateBoundary> = DatePeriod.entries.flatMap { period ->
            DateEdge.entries.map { edge -> TriggerDateBoundary(period, edge) }
        }
    }
}
