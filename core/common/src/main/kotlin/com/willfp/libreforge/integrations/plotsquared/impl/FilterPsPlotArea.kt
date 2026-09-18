package com.willfp.libreforge.integrations.plotsquared.impl

import com.plotsquared.core.events.PlotEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.integrations.plotsquared.asPlotSquaredEvent
import com.willfp.libreforge.triggers.TriggerData

object FilterPsPlotArea : Filter<NoCompileData, Collection<String>>("ps_plot_area") {
    override val description = "Matches when the PlotSquared plot involved in the trigger is in one of the given plot areas."
    override val categories = setOf("world")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf(
        "Requires the PlotSquared plugin.",
        "Plot areas are matched by world name, or by world;area for partial plot areas.",
        "Fails when the trigger has no PlotSquared plot attached to it."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event.asPlotSquaredEvent<PlotEvent>() ?: return false
        val area = event.plot?.area ?: return false

        return value.containsIgnoreCase(area.toString()) || value.containsIgnoreCase(area.worldName)
    }
}
